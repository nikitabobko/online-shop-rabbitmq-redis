package ru.bobko.shop.frontend.cli;

import com.google.gson.Gson;
import ru.bobko.shop.core.di.Injector;
import ru.bobko.shop.core.di.InjectorHolder;
import ru.bobko.shop.core.model.UserCart;
import ru.bobko.shop.core.model.message.Message;
import ru.bobko.shop.core.requestresponsecyclemanager.RequestResponseCycleManager;
import ru.bobko.shop.frontend.cli.base.CliCommand;
import ru.bobko.shop.frontend.cli.base.CliCommandAction;
import ru.bobko.shop.frontend.cli.base.CliException;
import ru.bobko.shop.util.FrontendModelUtil;

import java.util.Map;

import static java.util.stream.Collectors.joining;

public enum StatisticsCliCommand implements CliCommand, CliCommandAction {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "stat";
  }

  @Override
  public String getDescription() {
    return "Shows some online shop statistics";
  }

  @Override
  public String getCommandUsage() {
    return getCommandName();
  }

  @Override
  public CliCommandAction commandToActionNullable(String command) {
    return CliCommand.commandToActionNoCliArgumentsNullable(this, command, this);
  }

  @Override
  public String execAndGetOutput() {
    Injector injector = InjectorHolder.getInjector();
    UserCart cart = injector.getCart();
    RequestResponseCycleManager manager = injector.getRequestResponseCycleManager();
    Message request = Message.newRequest(Message.Type.STATISTICS, cart.getClientId(), null);
    Map<String, Double> stat = FrontendModelUtil.reactServerDoesntRespondOnInterruptedException(() -> {
      Message response = manager.requestResponseCycleToBackend(request);
      if (response.status != Message.Status.OK) {
        throw new CliException(response.additionalMsgNullable);
      }
      return new Gson().<Map<String, Double>>fromJson(response.additionalMsgNullable, Map.class);
    });
    return stat.entrySet().stream().map(it -> it.getKey() + "  :  " + it.getValue()).sorted().collect(joining("\n"));
  }
}
