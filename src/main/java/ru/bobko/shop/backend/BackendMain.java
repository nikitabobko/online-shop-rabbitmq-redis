package ru.bobko.shop.backend;

import ru.bobko.shop.backend.di.BackendInjector;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.core.model.message.Message;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackendMain {
  private final static ExecutorService pool = Executors.newSingleThreadExecutor();

  // todo heartbeat
  public static void main(String[] args) {
    InjectorHolder.initInjector(new BackendInjector((message) -> {
      pool.submit(() -> BackendMain.processIncomingMessage(message));
    }));
    Injector injector = InjectorHolder.getInjector();
    Warehouse warehouse = injector.getWarehouse();
    fillWarehouse(warehouse);
    System.out.println("Up and running");
  }

  private static void fillWarehouse(Warehouse warehouse) {
    warehouse.addGood(new Good("apple-421", "Apple", 10, setOf("Food")), 100);
    warehouse.addGood(new Good("limon-3141", "Limon", 10, setOf("Food")), 20);
    warehouse.addGood(new Good("elon-musk-truck-445", "Cybertruck", 39000, setOf("Cars", "Truck")), 1);
  }

  private static Set<String> setOf(String... members) {
    return new HashSet<>(Arrays.asList(members));
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
