package ru.bobko.shop.util;

import java.util.concurrent.TimeUnit;

public enum ThreadUtil {
  ;

  public static <T> T interruptSupplierIn(long time, TimeUnit unit, InterruptableSupplier<T> supplier) throws InterruptedException {
    Thread threadToInterrupt = Thread.currentThread();
    Thread threadWhichInterruptsUs = new Thread(() -> {
      try {
        unit.sleep(time);
        if (!Thread.interrupted()) {
          threadToInterrupt.interrupt();
        }
      } catch (InterruptedException ignored) {
      }
    });
    threadWhichInterruptsUs.start();
    T result = supplier.get();
    threadWhichInterruptsUs.interrupt();
    //noinspection ResultOfMethodCallIgnored
    Thread.interrupted();
    return result;
  }
}
