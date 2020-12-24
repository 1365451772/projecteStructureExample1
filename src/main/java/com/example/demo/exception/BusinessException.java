package com.example.demo.exception;


import lombok.NoArgsConstructor;


@NoArgsConstructor
public class BusinessException extends RuntimeException {

  private static final long serialVersionUID = 2038875170079852883L;

  /**
   * 构造函数初始化异常对象
   *
   * @param message 异常信息
   */
  public BusinessException(String message) {
    super(message);
  }

  /**
   * 构造函数初始化异常对象
   *
   * @param message 异常消息
   * @param cause 异常堆栈信息
   */
  public BusinessException(String message, Throwable cause) {
    super(message, cause);
  }


}
