package ru.bobko.shop.util;

public class ObjectUtil {
  public static <T> T chooseNotNull(T foo, T bar) {
    return foo != null ? foo : bar;
  }
}
