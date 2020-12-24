package com.example.demo.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qingbin.zeng
 * @date created in 2020/12/1 7:41 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "t_firmware_info")
public class FirmwareInfoEntity extends BaseEntity {

  private static final long serialVersionUID = -6911087029554388706L;

  @Column(name = "file_name", columnDefinition = "varchar(255) COMMENT '固件文件名称'")
  private String fileName;

  @Column(name = "file_path", columnDefinition = "varchar(255) COMMENT '固件存放路径'")
  private String filePath;

  @Column(name = "file_md5", columnDefinition = "varchar(64) COMMENT 'fileMd5'")
  private String fileMd5;

  @Column(name = "file_size", columnDefinition = "varchar(64) COMMENT 'fileSize'")
  private String fileSize;

  @Column(name = "file_type", columnDefinition = "varchar(20) COMMENT 'fileType'")
  private String fileType;

  @Column(name = "website", columnDefinition = "varchar(255) COMMENT '下载网址'")
  private String website;

  @Column(name = "source", columnDefinition = "varchar(10) COMMENT '来源'")
  private String source;

  @Column(name = "remark", columnDefinition = "varchar(255) COMMENT '备注'")
  private String remark;


}
