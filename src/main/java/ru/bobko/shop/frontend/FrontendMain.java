package ru.bobko.shop.frontend;

import com.rabbitmq.client.Channel;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliException;
import ru.bobko.shop.frontend.di.FrontendInjector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FrontendMain {
  private static void printResponse(String response) {
    System.out.println(Arrays.stream(response.split("\n")).map(it -> "  " + it).collect(Collectors.joining("\n")));
  }

  public static void main(String[] args) throws IOException {
    InjectorHolder.initInjector(new FrontendInjector(FrontendMain::messageFromServerCome));
    System.out.println("Hi!");
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
      while (true) {
        System.out.print("> ");
        System.out.flush();
        String line = reader.readLine();
        if (line == null || "".equals(line)) {
          break;
        }
        try {
          printResponse(CliCommand.commandToAction(line).execAndGetOutput());
        } catch (CliException ex) {
          printResponse(ex.getMessage());
          if (InjectorHolder.getInjector().isDebug()) {
            ex.printStackTrace();
          }
        }
      }
    }
  }

  private static void messageFromServerCome(Message message) {
    Injector injector = InjectorHolder.getInjector();
    if (message.type == Message.Type.HEARTH_BEAT && message.status == Message.Status.REQUEST) {
      try {
        Channel channel = injector.getChannel();
        message.respondOk().sendToBackend(channel);
      } catch (IOException e) {
        System.exit(1);
      }
      return;
    }
    injector.getRequestResponseCycleManager().notifyResponseCome(message);
  }
}
