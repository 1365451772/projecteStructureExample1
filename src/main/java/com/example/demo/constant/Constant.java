package com.example.demo.constant;


import java.io.File;


public class Constant {

  public static final String ROOT_PATH = System.getProperty("user.dir");

  public static final String TEMP_PATH = ROOT_PATH + File.separator + "temp";
  /**
   * 获取当前用户的家目录
   */
  public static final String USER_HOME = System.getProperty("user.home");

  public static final String STR_POINT = ".";
}
