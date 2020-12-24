package com.example.demo.service;


import com.example.demo.model.entity.FirmwareInfoEntity;
import com.example.demo.model.form.FirmwareInfoForm;
import com.example.demo.model.response.BaseResponse;
import com.example.demo.model.response.ListResult;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;


public interface FirmwareService {


  /**
   * 查询固件列表
   * @return listResult
   */
  ListResult<FirmwareInfoEntity> getFirmwareInfoList(String firmwareName, String startDate,
      String endDate,
      String fileType, int offset, int limit, String order, String sort);
 
  /*
   *@Description: 更新firmware信息
   *@Param: [firmwareInfoForm]
   *@return: int
   *@Author:
   *@Date: 2020/12/7
  */
  int update(FirmwareInfoForm firmwareInfoForm);
  
  /*
   *@Description: 添加firmware信息
   *@Param: [firmwareInfoForm]
   *@return: void
   *@Author:
   *@Date: 2020/12/7
  */
  void save(FirmwareInfoForm firmwareInfoForm);

  /*
   *@Description: 删除firmware信息
   *@Param: [id]
   *@return:int
   *@Author: sp
   *@Date: 2020/12/7
  */
  int  deleteById(Long id);
  /*
   *@Description: 根据id得到firmware信息
   *@Param: [id]
   *@return: void
   *@Author: sp
   *@Date: 2020/12/7
  */
 FirmwareInfoEntity getFirmwareById(Long id);

  BaseResponse upload(String filePath, MultipartFile multipartFile) throws IOException;

  BaseResponse update(Long firmwareId, String filePath);

//  BaseResponse getFirmwareFileTypeList();
}
