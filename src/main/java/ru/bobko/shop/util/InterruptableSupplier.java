package ru.bobko.shop.util;

@FunctionalInterface
public interface InterruptableSupplier<T> {
  T get() throws InterruptedException;
}
