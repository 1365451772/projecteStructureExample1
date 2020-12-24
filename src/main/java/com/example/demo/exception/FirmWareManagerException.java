package com.example.demo.exception;

import lombok.NoArgsConstructor;

/**
 * @Author sp
 * @Description
 * @create 2020-12-07 11:25
 * @Modified By:
 */
@NoArgsConstructor
public class FirmWareManagerException extends RuntimeException  {
  private static final long serialVersionUID = 6806148693365111034L;


  /*
   *@Description: 构造函数初始化异常对象
   *@Param: [message]
   *@return:
   *@Author:
   *@Date: 2020/12/7
  */
  public FirmWareManagerException(String message){
    super(message);
  }

  /*
   *@Description: 构造函数初始化异常对象
   *@Param: [message, cause]
   *@return:
   *@Author: sp
   *@Date: 2020/12/7
  */
  public FirmWareManagerException(String message,Throwable cause){
    super(message,cause);
  }
}
