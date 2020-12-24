package com.example.demo.model.entity;

import com.alibaba.fastjson.annotation.JSONField;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.Data;

/**
 *
 */
@Data
@MappedSuperclass
public class BaseEntity implements Serializable {

  private static final long serialVersionUID = 7624552093946377161L;

  /**
   * 主键自增
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  /**
   * 创建时间
   */
  @Column(name = "create_time")
  @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
  private Date createTime = new Date();

  /**
   * 上一次数据修改时间
   */
  @Column(name = "last_update_time")
  @JSONField(name = "last_update_time", format = "yyyy-MM-dd HH:mm:ss")
  private Date lastUpdateTime;

}
