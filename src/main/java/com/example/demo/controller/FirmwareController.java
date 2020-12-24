package com.example.demo.controller;

import com.example.demo.model.entity.FirmwareInfoEntity;
import com.example.demo.model.response.BaseResponse;
import com.example.demo.model.response.ListResult;
import com.example.demo.service.FirmwareService;
import com.example.demo.util.MinioUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RequestMapping("/api/v1/firmware")
@Api(tags = "固件信息接口")
@Slf4j
@RestController
public class FirmwareController {

  @Autowired
  private FirmwareService firmwareService;
  @Autowired
  private MinioUtil minioUtil;

  /**
   * 引擎任务列表查询
   */
  @ApiOperation(value = "固件列表查询", produces = "application/json")
  @PostMapping(value = "/info/list")
  public BaseResponse firmwareInfoList(
      @RequestParam(name = "firmware_name", required = false) String firmwareName,
      @RequestParam(name = "file_type", required = false) String fileType,
      @RequestParam(name = "start_date", required = false) String startDate,
      @RequestParam(name = "end_date", required = false) String endDate,
      @RequestParam(name = "offset", defaultValue = "1", required = false) int offset,
      @RequestParam(name = "limit", defaultValue = "20", required = false) int limit,
      @RequestParam(name = "order", defaultValue = "createTime", required = false) String order,
      @RequestParam(name = "sort", defaultValue = "desc", required = false) String sort) {
    if (firmwareName != null) {
      firmwareName = firmwareName.trim();
    }
    ListResult<FirmwareInfoEntity> listResult = firmwareService
        .getFirmwareInfoList(firmwareName, startDate, endDate, fileType, offset, limit, order,
            sort);
    return BaseResponse.success(listResult);

  }

  /*
   *@Description: 根据Id得到固件信息
   *@Param: [id]
   *@return: com.bangcle.firmware.spider.manager.model.response.BaseResponse
   *@Author:
   *@Date: 2020/12/7
   */
  @ApiOperation(value = "根据Id得到固件信息", produces = "application/json")
  @PostMapping(value = "/info/getFirmwareById/{id}")
  public BaseResponse getFirmwareById(@PathVariable(name = "id", required = true) Long id) {
    FirmwareInfoEntity firmware = firmwareService.getFirmwareById(id);
    return BaseResponse.success(firmware);
  }


  @ApiOperation(value = "固件信息更新", produces = "application/json")
  @PostMapping(value = "/info/update")
  public BaseResponse update(
      @RequestParam(value = "firmwareId", required = true) Long id,
      @RequestParam(value = "webSite", required = true) String webSite
  ) {
    return firmwareService.update(id, webSite);

  }

  @ApiOperation(value = "上传firmware", produces = "application/json")
  @PostMapping(value = "/info/upload")
  public BaseResponse upload(
      @RequestParam(value = "webSite", defaultValue = "", required = false) String webSite,
      @RequestParam(value = "file") MultipartFile multipartFile) throws IOException {
    return firmwareService.upload(webSite, multipartFile);
  }

  /*
   *@Description: 固件信息删除
   *@Param: [id]
   *@return: com.bangcle.firmware.spider.manager.model.response.BaseResponse
   *@Author:
   *@Date: 2020/12/7
   */
  @ApiOperation(value = "固件信息删除", produces = "application/json")
  @PostMapping(value = "/info/deleteById/{id}")
  public BaseResponse deleteFirmwarebyId(@PathVariable(name = "id", required = true) String id) {
    int result = firmwareService.deleteById(Long.valueOf(id));
    return result == 0 ? BaseResponse.error("未找到id对应的firmWare") : BaseResponse.success("");

  }
}
