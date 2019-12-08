package ru.bobko.shop.backend;

import ru.bobko.shop.backend.di.BackendInjector;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.message.Message;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackendMain {
  private final static ExecutorService pool = Executors.newSingleThreadExecutor();

  // todo heartbeat
  public static void main(String[] args) {
    InjectorHolder.initInjector(new BackendInjector((message) -> {
      pool.submit(() -> BackendMain.processIncomingMessage(message));
    }));
    System.out.println("Up and running");
  }

  private static void processIncomingMessage(Message message) {
    Injector injector = InjectorHolder.getInjector();
    if (message.status == Message.Status.OK && message.type == Message.Type.HEARTH_BEAT) {
      injector.getRequestResponseCycleManager().notifyResponseCome(message);
    }
    try {
      message.type.backendProcessRequest(message, injector.getChannel(), injector.getUsers(), injector.getWarehouse());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
