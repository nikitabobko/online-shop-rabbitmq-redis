package ru.bobko.shop.backend;

import ru.bobko.shop.backend.di.BackendInjector;
import ru.bobko.shop.backend.model.BackendUsers;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.Warehouse;
import ru.bobko.shop.core.model.good.Good;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackendMain {
  private final static ExecutorService workingPool = Executors.newSingleThreadExecutor();
  private final static ExecutorService hearthBeatPool = Executors.newSingleThreadExecutor();

  public static void main(String[] args) {
    InjectorHolder.initInjector(new BackendInjector((message) -> {
      if (message.type == Message.Type.HEARTH_BEAT) {
        processHeartBeatMessage(message);
      } else {
        workingPool.submit(() -> processIncomingMessage(message));
      }
    }));
    hearthBeatPool.submit(BackendMain::hearthBeatJob);
    Injector injector = InjectorHolder.getInjector();
    Warehouse warehouse = injector.getWarehouse();
    fillWarehouse(warehouse);
    System.out.println("Up and running");
  }

  private static void hearthBeatJob() {
    Injector injector = InjectorHolder.getInjector();
    BackendUsers users = injector.getUsers();
    RequestResponseCycleManager requestResponseCycleManager = injector.getRequestResponseCycleManager();
    while (true) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
        System.exit(1);
      }
      Set<UserCart> allUsers = users.getAllUsers();
      for (UserCart user : allUsers) {
        Message heartBeat = Message.newHearthBeatRequest(user.getClientId());
        try {
          requestResponseCycleManager.requestResponseCycle(user.getClientId(), heartBeat);
        } catch (InterruptedException ex) {
          if (injector.isDebug()) {
            System.out.println("User " + user.getClientId() + " doesn't respond. Discarting user's cart");
          }
          users.removeUser(user);
        }
      }
    }
  }

  private static void fillWarehouse(Warehouse warehouse) {
    warehouse.addGood(new Good("apple-421", "Apple", 10, setOf("Food")), 100);
    warehouse.addGood(new Good("limon-3141", "Limon", 10, setOf("Food")), 20);
    warehouse.addGood(new Good("elon-musk-truck-445", "Cybertruck", 39000, setOf("Cars", "Truck")), 1);
  }

  private static Set<String> setOf(String... members) {
    return new HashSet<>(Arrays.asList(members));
  }

  private static void processHeartBeatMessage(Message message) {
    assert message.type == Message.Type.HEARTH_BEAT;
    try {
      Injector injector = InjectorHolder.getInjector();
      if (message.status == Message.Status.REQUEST) {
        message.respondOk().sendTo(injector.getChannel(), message.clientId);
      } else {
        injector.getRequestResponseCycleManager().notifyResponseCome(message);
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private static void processIncomingMessage(Message request) {
    assert request.type != Message.Type.HEARTH_BEAT;
    Injector injector = InjectorHolder.getInjector();
    try {
      BackendUsers users = injector.getUsers();
      users.getOrRegisterUserById(request.clientId); // Register every incomming user for statistics
      request.type.backendProcessRequest(request, injector.getChannel(), users, injector.getWarehouse());
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
