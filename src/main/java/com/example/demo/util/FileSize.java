package com.example.demo.util;

import java.text.DecimalFormat;

/**
 * @Author
 * @Description
 * @create 2020-12-16 12:47
 * @Modified By:
 */
public class FileSize {
  public static String getNetFileSizeDescription(long size) {
    StringBuffer bytes = new StringBuffer();
    DecimalFormat format = new DecimalFormat("###.0");
    if (size >= 1024 * 1024 * 1024) {
      double i = (size / (1024.0 * 1024.0 * 1024.0));
      bytes.append(format.format(i)).append("GB");
    }
    else if (size >= 1024 * 1024) {
      double i = (size / (1024.0 * 1024.0));
      bytes.append(format.format(i)).append("MB");
    }
    else if (size >= 1024) {
      double i = (size / (1024.0));
      bytes.append(format.format(i)).append("KB");
    }
    else if (size < 1024) {
      if (size <= 0) {
        bytes.append("0B");
      }
      else {
        bytes.append((int) size).append("B");
      }
    }
    return bytes.toString();
  }
}
