package com.example.demo.service.imp;

import ch.qos.logback.core.util.FileSize;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.MD5;
import com.example.demo.constant.Constant;
import com.example.demo.constant.ResponseCode;
import com.example.demo.enums.SourceEnum;
import com.example.demo.exception.FirmWareManagerException;
import com.example.demo.model.entity.FirmwareInfoEntity;
import com.example.demo.model.form.FirmwareInfoForm;
import com.example.demo.model.response.BaseResponse;
import com.example.demo.model.response.ListResult;
import com.example.demo.repository.FirmwareInfoRepo;
import com.example.demo.service.BaseService;
import com.example.demo.service.FirmwareService;
import com.example.demo.util.MinioUtil;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
public class FirmwareServiceImpl extends BaseService implements FirmwareService {

  @Autowired
  private MinioUtil minioUtil;
  @Autowired
  private FirmwareInfoRepo firmwareInfoRepo;


  @Override
  public ListResult<FirmwareInfoEntity> getFirmwareInfoList(String firmwareName, String startDate,
      String endDate, String fileType, int offset, int limit, String order, String sort) {

    String sql = "select t.* from t_firmware_info t where 1=1";
    String sortSql = " order by " + humpToLine2(order) + " " + sort;
    List<String> paramList = new ArrayList<>();
    if (StringUtils.isNotBlank(firmwareName)) {
      sql += " and t.file_name like ?";
      paramList.add("%" + firmwareName + "%");
    }
    if (StringUtils.isNotBlank(fileType)) {
      sql += " and t.file_type like ?";
      paramList.add("%" + fileType + "%");
    }
    if (StringUtils.isNotBlank(startDate)) {
      sql += " and DATE_FORMAT(create_time ,'%Y-%m-%d') >= ?";
      paramList.add(startDate);
    }
    if (StringUtils.isNotBlank(endDate)) {
      sql += " and DATE_FORMAT(create_time ,'%Y-%m-%d') <= ? ";
      paramList.add(endDate);
    }
    ListResult<FirmwareInfoEntity> listResult = pageBySql(sql, sortSql, paramList.toArray(), offset,
        limit,
        FirmwareInfoEntity.class);

    return listResult;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int update(FirmwareInfoForm firmwareInfoForm) {
    Long firmwareId = firmwareInfoForm.getId();
    Optional<FirmwareInfoEntity> selectedFirmware = firmwareInfoRepo.findById(firmwareId);
    if (!selectedFirmware.isPresent()) {
      return 0;
    }
    Date date = new Date();
    FirmwareInfoEntity firmwareInfoEntity = selectedFirmware.get();
    if (!firmwareInfoForm.equalsEntity(firmwareInfoEntity)) {
      BeanUtils.copyProperties(firmwareInfoForm, firmwareInfoEntity);
      firmwareInfoEntity.setLastUpdateTime(date);
      firmwareInfoRepo.saveAndFlush(firmwareInfoEntity);

    }
    return 1;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void save(FirmwareInfoForm firmwareInfoForm) {
    Date date = new Date();
    FirmwareInfoEntity firmwareInfoEntity = new FirmwareInfoEntity();
    firmwareInfoEntity.setFileMd5(firmwareInfoForm.getFileMd5());
    firmwareInfoEntity.setFileName(firmwareInfoForm.getFileName());
    firmwareInfoEntity.setFilePath(firmwareInfoForm.getFilePath());
    firmwareInfoEntity.setFileSize(firmwareInfoForm.getFileSize());
    firmwareInfoEntity.setFileType(firmwareInfoForm.getFileType());
    firmwareInfoEntity.setRemark(firmwareInfoForm.getRemark());
    firmwareInfoEntity.setWebsite(firmwareInfoForm.getWebsite());
    firmwareInfoEntity.setCreateTime(date);
    firmwareInfoRepo.save(firmwareInfoEntity);


  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public int deleteById(Long id) {
    {

      Optional<FirmwareInfoEntity> selectedFirmware = firmwareInfoRepo.findById(id);
      if (!selectedFirmware.isPresent()) {
        return 0;
      }
      firmwareInfoRepo.deleteById(id);
      return 1;
    }

  }

  @Override
  public FirmwareInfoEntity getFirmwareById(Long id) {
    Optional<FirmwareInfoEntity> selectedFirmware = firmwareInfoRepo.findById(id);
    if (!selectedFirmware.isPresent()) {
      throw new FirmWareManagerException("未找到id对应的firmWare");
    }
    return selectedFirmware.get();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BaseResponse upload(String webSite, MultipartFile multipartFile) throws IOException {
    String originalFilename = multipartFile.getOriginalFilename();
    if (originalFilename == null) {
      return BaseResponse.error(ResponseCode.ERROR_CODE_1001, "文件名获取失败");
    }
    String fileExt = FileUtil.extName(originalFilename);
    String md5 = MD5.create().digestHex(multipartFile.getInputStream());
    long size = multipartFile.getSize();

    FirmwareInfoEntity firmwareInfoEntity = new FirmwareInfoEntity();
    synchronized (md5.intern()) {
      String saveName = md5 + Constant.STR_POINT + fileExt;
      boolean uploadResult = minioUtil.upload(minioUtil.getFirmwareBucketName(), saveName,
          multipartFile.getInputStream(), multipartFile.getContentType());
      if (uploadResult) {
        firmwareInfoEntity.setCreateTime(new Date());
        firmwareInfoEntity.setFileType(fileExt);
        firmwareInfoEntity.setFileMd5(md5);
        firmwareInfoEntity.setWebsite(webSite);
        firmwareInfoEntity.setFilePath(saveName);
        firmwareInfoEntity.setFileName(originalFilename);
        firmwareInfoEntity.setSource(SourceEnum.WEB.getDesc());
        firmwareInfoRepo.save(firmwareInfoEntity);
//        if (firmwareFileTypeInfoRepo.getByFileType(fileExt)==null){
//          FirmwareFileTypeInfoEntity firmwareFileTypeInfoEntity = new FirmwareFileTypeInfoEntity();
//          firmwareFileTypeInfoEntity.setFileType(fileExt);
//          firmwareFileTypeInfoRepo.save(firmwareFileTypeInfoEntity);
//        }
        return BaseResponse.success("success");
      } else {
        return BaseResponse.error(ResponseCode.ERROR_CODE_500, originalFilename, "上传失败");
      }

    }


  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public BaseResponse update(Long firmwareId, String webSite) {
    Optional<FirmwareInfoEntity> selectedFirmware = firmwareInfoRepo.findById(firmwareId);
    if (!selectedFirmware.isPresent()) {
      return BaseResponse.error("未找到id对应的firmWare");
    }
    FirmwareInfoEntity firmwareInfo = selectedFirmware.get();
    firmwareInfo.setWebsite(webSite);
    firmwareInfo.setLastUpdateTime(new Date());
    firmwareInfoRepo.saveAndFlush(firmwareInfo);
    return BaseResponse.success("success");
  }

//  @Override
//  public BaseResponse getFirmwareFileTypeList() {
//    List<String> result = firmwareFileTypeInfoRepo.getFirmwareFileTypeList();
//    if (result != null && result.size() > 0) {
//      return BaseResponse.success(result);
//    }
//    return BaseResponse.success("");
//  }

}
