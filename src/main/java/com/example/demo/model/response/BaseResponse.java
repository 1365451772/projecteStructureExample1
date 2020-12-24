package com.example.demo.model.response;


import com.example.demo.constant.ResponseCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 公共响应
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse<T> extends ResponseCode {

  @NotNull(message = "[code]code编码不能为空")
  private int code = SUCCESS;
  @JsonProperty("request_id")
  private String requestId;
  private String msg;
  /**
   * 描述信息
   */
  private T data;

  public boolean success() {
    return this.code == ResponseCode.SUCCESS;
  }

  public boolean fail() {
    return this.code != ResponseCode.SUCCESS;
  }

  public static <T> BaseResponse<T> success(T data) {
    return BaseResponse.<T>builder().code(SUCCESS).data(data).build();

  }

  public static <T> BaseResponse<T> success(String requestId, T data) {
    return BaseResponse.<T>builder().code(SUCCESS).requestId(requestId).data(data).build();
  }

  public static <T> BaseResponse<T> success(String requestId, String msg) {
    return BaseResponse.<T>builder().code(SUCCESS).requestId(requestId).msg(msg)
        .build();
  }

  public static <T> BaseResponse<T> success(String requestId, String msg, T data) {
    return BaseResponse.<T>builder().code(SUCCESS).requestId(requestId).msg(msg)
        .data(data).build();
  }

  public static <T> BaseResponse<T> repeat(String requestId, String msg, T data) {
    return BaseResponse.<T>builder()
        .code(ERROR_CODE_1005)
        .requestId(requestId)
        .msg(msg)
        .data(data)
        .build();
  }

  public static <T> BaseResponse<T> error(String msg) {
    return BaseResponse.<T>builder().code(ERROR).msg(msg)
        .build();
  }

  public static <T> BaseResponse<T> error(int code, String requestId, String msg) {
    return BaseResponse.<T>builder().code(code).requestId(requestId).msg(msg)
        .build();
  }

  public static <T> BaseResponse<T> error(int code, String msg) {
    return BaseResponse.<T>builder().code(code).msg(msg)
        .build();
  }

}