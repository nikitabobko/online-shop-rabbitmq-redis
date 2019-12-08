package ru.bobko.shop.util;

import ru.bobko.shop.frontend.cli.base.CliException;

public enum FrontendModelUtil {
  ;

  public static <T> T reactServerDoesntRespondOnInterruptedException(InterruptableSupplier<T> supplier) {
    try {
      return supplier.get();
    } catch (InterruptedException e) {
      throw new CliException.ServerDoesntRespondCliException();
    }
  }
}
