package com.example.demo.constant;

/**
 * https://www.jianshu.com/p/27b64309ec17
 *
 *
 */
public class ResponseCode {

  public static final int SUCCESS = 0;
  public static final int ERROR = 1;
  // 内部错误
  public static final int ERROR_CODE_500 = 500;
  // 系统自定义异常
  public static final int ERROR_CODE_1000 = 1000;
  // 格式错误
  public static final int ERROR_CODE_1001 = 1001;
  // 重复提交
  public static final int ERROR_CODE_1005 = 1005;
  // 文件下载失败
  public static final int ERROR_CODE_1007 = 1007;
  // 引擎网络异常
  public static final int ERROR_CODE_1009 = 1009;
  // 任务处理超时
  public static final int ERROR_CODE_1013 = 1013;
  // 文件上传到minio失败
  public static final int ERROR_CODE_1015 = 1015;

}
