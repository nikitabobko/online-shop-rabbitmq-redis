package ru.bobko.shop.util;

import java.util.Objects;
import java.util.function.Supplier;

// todo remove
public class NotNullLazyValue<T> {
  private final Supplier<T> supplier;
  private volatile T value;

  private NotNullLazyValue(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public T getValue() {
    if (value != null) {
      return value;
    }
    synchronized (supplier) {
      if (value != null) {
        return value;
      }
      value = supplier.get();
    }
    return Objects.requireNonNull(value);
  }

  public static <T> NotNullLazyValue<T> newInstance(Supplier<T> supplier) {
    return new NotNullLazyValue<>(supplier);
  }
}
