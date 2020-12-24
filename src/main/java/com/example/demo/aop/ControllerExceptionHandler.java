package com.example.demo.aop;


import com.example.demo.constant.ResponseCode;
import com.example.demo.exception.BusinessException;
import com.example.demo.model.response.BaseResponse;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;



@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  @ExceptionHandler(Throwable.class)
  @ResponseBody
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public BaseResponse processThrowable(Throwable ex) {
    log.error("服务器内部异常", ex);
    return BaseResponse.error(ResponseCode.ERROR_CODE_500, null, "服务端内部异常，请联系管理员");
  }

  /**
   * 一般的参数绑定时候抛出的异常
   */
  @ExceptionHandler(value = BindException.class)
  @ResponseBody
  public BaseResponse handleBindException(BindException ex) {

    String defaultMsg = ex.getBindingResult().getAllErrors()
        .stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.joining(" | ", "", ""));
    log.error("参数校验异常", defaultMsg);
    return BaseResponse.error(ResponseCode.ERROR_CODE_1001, null, defaultMsg);
  }

  /**
   * 一般的参数绑定时候抛出的异常
   */
  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  public BaseResponse handleBindException(MethodArgumentNotValidException ex) {

    String defaultMsg = ex.getBindingResult().getAllErrors()
        .stream()
        .map(ObjectError::getDefaultMessage)
        .collect(Collectors.joining(" | ", "", ""));
    log.error("参数校验异常:{}", defaultMsg);
    return BaseResponse.error(ResponseCode.ERROR_CODE_1001, null, defaultMsg);
  }

  /**
   * 单个参数校验
   */
  @ExceptionHandler(value = ConstraintViolationException.class)
  @ResponseBody
  public BaseResponse handleBindGetException(ConstraintViolationException ex) {

    String defaultMsg = ex.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(" | ", "", ""));
    log.error("单个参数校验异常:{}", defaultMsg);
    return BaseResponse.error(ResponseCode.ERROR_CODE_1001, null, defaultMsg);
  }

  /**
   * 自定义异常
   */
  @ExceptionHandler(value = BusinessException.class)
  @ResponseBody
  public BaseResponse handleBusinessException(BusinessException ex) {
    String defaultMsg = ex.getMessage();
    log.error("系统自定义异常:{}", defaultMsg);
    return BaseResponse.error(ResponseCode.ERROR_CODE_1000, null, defaultMsg);
  }

  /**
   * 格式错误
   *
   * @param ex
   */
  @ExceptionHandler(value = HttpMessageNotReadableException.class)
  @ResponseBody
  public BaseResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
    String message = ex.getCause().getMessage();
    return BaseResponse.error(ResponseCode.ERROR_CODE_1001, null, message);
  }


}
