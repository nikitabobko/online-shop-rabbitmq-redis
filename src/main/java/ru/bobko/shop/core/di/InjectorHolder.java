package ru.bobko.shop.core.di;

public enum InjectorHolder {
  ;
  private static volatile Injector injector;

  public static Injector getInjector() {
    if (injector == null) {
      throw new IllegalStateException("Injector must be initialized firslty");
    }
    return injector;
  }

  public static synchronized void initInjector(Injector injector) {
    if (InjectorHolder.injector != null) {
      throw new IllegalStateException("Injector must be initialized only once");
    }
    InjectorHolder.injector = injector;
  }

  static boolean isInitialized() {
    return injector != null;
  }
}
