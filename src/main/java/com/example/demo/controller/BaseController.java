package com.example.demo.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * @Author sp
 * @Description  公共Controller，所有业务类Controller继承该类 封装常用操作
 * @create 2021-01-05 10:02
 * @Modified By:
 */
@Slf4j
@Controller
public class BaseController {

  @Autowired
  public HttpServletRequest request;
  @Autowired
  public HttpServletResponse response;

  public static Map<String, String> mimeMap = new HashMap();

  public void downloadFile(File file) throws Exception {
    downloadFile(file, null, null);
  }

  public void downloadFile(File file, String reFileName, String contentType) throws Exception {

    if (StringUtils.isBlank(reFileName)) {
      reFileName = file.getName();
    }

    if (contentType == null) {
      contentType = getContentType(reFileName);
      if (contentType == null) {
        contentType = new MimetypesFileTypeMap().getContentType(file.getName());// name:"aa.txt"
      }
    }
    OutputStream os = null;
    FileInputStream fis = null;
    try {
      os = response.getOutputStream();
      String userAgent = request.getHeader("User-Agent");
      if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
        reFileName = URLEncoder.encode(reFileName, "UTF-8");
      } else {
        reFileName = new String(reFileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
      }
      if (!StringUtils.isBlank(contentType)) {
        response.setContentType(contentType);
      } else {
        response.setContentType("application/octet-stream;charset=utf-8");
      }
      response.setHeader("Content-Disposition", "attachment; filename=" + reFileName);
      response.setCharacterEncoding("UTF-8");
      fis = new FileInputStream(file);
      byte[] buf = new byte[1024];
      int len = 0;
      while ((len = fis.read(buf)) != -1) {
        os.write(buf, 0, len);
      }
    } catch (Exception e) {
      log.error("下载异常", e);
      throw e;
    } finally {
      IOUtils.closeQuietly(os);
      IOUtils.closeQuietly(fis);
    }
  }

  public void downloadFile(InputStream inputStream, String fileName) throws Exception {

    String contentType = getContentType(fileName);
    if (contentType == null) {
      contentType = new MimetypesFileTypeMap().getContentType(fileName);// name:"aa.txt"
    }

    OutputStream os = null;
    try {
      os = response.getOutputStream();
      String userAgent = request.getHeader("User-Agent");
      if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
        fileName = URLEncoder.encode(fileName, "UTF-8");
      } else {
        fileName = new String(fileName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
      }
      if (!StringUtils.isBlank(contentType)) {
        response.setContentType(contentType);
      } else {
        response.setContentType("application/octet-stream;charset=utf-8");
      }
      response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
      response.setCharacterEncoding("UTF-8");
      byte[] buf = new byte[1024];
      int len = 0;
      while ((len = inputStream.read(buf)) != -1) {
        os.write(buf, 0, len);
      }
    } catch (Exception e) {
      log.error("下载异常", e);
      throw e;
    } finally {
      IOUtils.closeQuietly(os);
      IOUtils.closeQuietly(inputStream);
    }
  }

  private String getContentType(String filename) {
    if (mimeMap == null || mimeMap.size() == 0) {
      mimeMap.put("pdf", "application/pdf");
      mimeMap.put("doc", "application/msword");
      mimeMap.put("docx",
          "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
      mimeMap.put("htm", "text/html");
      mimeMap.put("html", "text/html");
      mimeMap.put("txt", "text/plain");
      mimeMap.put("log", "text/plain");
      mimeMap.put("zip", "application/zip");
      mimeMap.put("asc", "application/zip");
      mimeMap.put("rar", "application/x-rar-compressed");
      mimeMap.put("xls", "application/vnd.ms-excel");
      mimeMap.put("ppt", "application/vnd.ms-powerpoint");
      mimeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    }

    int dot = filename.lastIndexOf(".");
    if (dot < 0) {
      return null;
    }
    String fileType = filename.substring(dot + 1);
    return mimeMap.get(fileType);

  }

}
