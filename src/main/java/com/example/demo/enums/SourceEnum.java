package com.example.demo.enums;


public enum SourceEnum {
  /**
   * 信息来源
   */
  WEB("web", "web系统"),

  SPIDER("spider", "爬虫");

  private final String code;
  private final String desc;

  SourceEnum(String code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public static String getDesc(String code) {
    SourceEnum[] values = SourceEnum.values();
    for (SourceEnum value : values) {
      if (value.getCode().equals(code)) {
        return value.getDesc();
      }
    }
    return "";
  }

  public String getCode() {
    return this.code;
  }

  public String getDesc() {
    return this.desc;
  }
}
