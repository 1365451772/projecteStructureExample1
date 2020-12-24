package com.example.demo.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import com.example.demo.exception.BusinessException;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author qingbin.zeng
 * @date created in 2020/7/14 10:38 上午
 */
@Slf4j
@Component
public class MinioUtil {

  @Autowired
  private MinioClient minioClient;

  @Getter
  @Value("firmware-spider")
  private String firmwareBucketName;


  @Getter
  @Value("TBD")
  private String tempDirPath;

  public boolean upload(@NotEmpty String bucketName, @NotEmpty String fileName,
      @NotNull InputStream inputStream,
      String contentType) {
    try {
      boolean isExist = minioClient.bucketExists(bucketName);
      if (!isExist) {
        minioClient.makeBucket(bucketName);
      }
      minioClient.putObject(bucketName, fileName, inputStream, contentType);
    } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException | XmlPullParserException e) {
      log.error("upload to minio exception", e);
      return false;
    }
    return true;
  }

  /**
   * 下载文件·
   *
   * @param bucketName bucketName
   * @param fileName   fileName
   * @return stream
   */
  public InputStream downloadAsStream(String bucketName, String fileName) {
    Assert.notEmpty(bucketName);
    Assert.notEmpty(fileName);

    try {
      return minioClient.getObject(bucketName, fileName);
    } catch (MinioException | NoSuchAlgorithmException | IOException | InvalidKeyException | XmlPullParserException e) {
      log.error("download file from minio exception", e);
      throw new BusinessException("下载文件失败");
    }
  }

  /**
   * 下载文件到本地文件夹下
   *
   * @param bucketName bucketName
   * @param fileName   minio上文件名
   * @param localPath  下载路径
   * @return 下载后文件路径
   */
  public String downloadToLocal(String bucketName, String fileName, String localPath) {
    InputStream inputStream = downloadAsStream(bucketName, fileName);
    try {
      File file = new File(localPath);
      // 判断localPath文件路径是否存在
      if (!file.exists()) {
        FileUtil.mkdir(localPath);
      }
      localPath = localPath + File.separator + fileName;
      File targetFile = new File(localPath);
      FileUtil.writeFromStream(inputStream, targetFile);
    } finally {
      IoUtil.close(inputStream);
    }
    return localPath;
  }

  /**
   * 下载文件到本地文件夹下
   *
   * @param bucketName bucketName
   * @param fileName   minio上文件名
   * @param localFilePath  下载文件路径
   * @return 下载后文件路径
   */
  public String downloadToLocalFile(String bucketName, String fileName, String localFilePath) {
    InputStream inputStream = downloadAsStream(bucketName, fileName);
    try {
      File file = new File(localFilePath);
      // 判断localPath文件路径是否存在
      if (!file.exists()) {
        FileUtil.mkdir(FileUtil.getParent(localFilePath,1));
      }
      File targetFile = new File(localFilePath);
      FileUtil.writeFromStream(inputStream, targetFile);
    } finally {
      IoUtil.close(inputStream);
    }
    return localFilePath;
  }


  /**
   *
   * @param bucketName
   * @param fileName
   * @param localPath
   * @return
   */
  public File downloadToLocalAndReturnFile(String bucketName, String fileName, String localPath) {
    InputStream inputStream = downloadAsStream(bucketName, fileName);
    try {
      File file = new File(localPath);
      // 判断localPath文件路径是否存在
      if (!file.exists()) {
        FileUtil.mkdir(localPath);
      }
      localPath = localPath + File.separator + fileName;
      File targetFile = new File(localPath);
      FileUtil.writeFromStream(inputStream, targetFile);
      return targetFile;
    } finally {
      IoUtil.close(inputStream);
    }
  }




}
