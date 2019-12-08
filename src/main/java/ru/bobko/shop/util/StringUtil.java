package ru.bobko.shop.util;

public enum StringUtil {
  ;

  public static boolean isEmpty(String str) {
    return str == null || "".equals(str);
  }
}
