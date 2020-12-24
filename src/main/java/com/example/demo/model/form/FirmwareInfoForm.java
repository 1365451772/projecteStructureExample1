package com.example.demo.model.form;

import com.alibaba.fastjson.annotation.JSONField;
import com.example.demo.model.entity.FirmwareInfoEntity;
import java.util.Objects;
import lombok.Data;

/**
 * @Author
 * @Description
 * @create 2020-12-07 10:25
 * @Modified By:
 */
@Data
public class FirmwareInfoForm {

  /*
   * 固件id
   * */
  @JSONField(name = "firware_id")
  private Long id;

  @JSONField(name = "file_name")
  private String fileName;

  @JSONField(name = "file_path")
  private String filePath;


  @JSONField(name = "file_md5")
  private String fileMd5;

  @JSONField(name = "file_size")
  private String fileSize;

  @JSONField(name = "file_type")
  private String fileType;

  @JSONField(name = "website")
  private String website;

  @JSONField(name = "source")
  private String source;

  @JSONField(name = "remark")
  private String remark;


  public boolean equalsEntity(FirmwareInfoEntity that) {
    return Objects.equals(fileName, that.getFileName()) &&
        Objects.equals(fileMd5, that.getFileMd5()) &&
        Objects.equals(filePath, that.getFilePath()) &&
        Objects.equals(fileSize, that.getFileSize()) &&
        Objects.equals(fileType, that.getFileType()) &&
        Objects.equals(website, that.getWebsite()) &&
        Objects.equals(source, that.getSource()) &&
        Objects.equals(remark, that.getRemark());

  }
}
