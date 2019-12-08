package ru.bobko.shop.frontend.cli.base;

import ru.bobko.shop.frontend.cli.AddToCardCliCommand;
import ru.bobko.shop.frontend.cli.RemoveFromCartCliCommand;
import ru.bobko.shop.frontend.cli.ShowAllCliCommand;
import ru.bobko.shop.frontend.cli.ShowCartCliCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface CliCommand {
  List<CliCommand> registered = Arrays.asList(
    AddToCardCliCommand.INSTANCE,
    RemoveFromCartCliCommand.INSTANCE,
    ShowCartCliCommand.INSTANCE,
    ShowAllCliCommand.INSTANCE,
    HelpCliCommand.INSTANCE
  );

  String getCommandName();

  String getDescription();

  String getCommandUsage();

  default String getHelp() {
    return String.format("%s - %s\nUSAGE:\n> %s", getCommandName(), getDescription(), getCommandUsage());
  }

  CliCommandAction commandToActionNullable(String command);

  static CliCommandAction commandToAction(String line) {
    String command = line.trim();
    return registered.stream()
      .map(it -> it.commandToActionNullable(command))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(new NoSuchCliCommandCliCommandAction(command));
  }

  static CliCommandAction commandToActionOneCliArgumentNullable(CliCommand info,
                                                                String command,
                                                                Function<String, CliCommandAction> supplier) {
    String[] split = command.split("\\s+");
    if (split.length != 2) {
      return null;
    }
    if (!split[0].equals(info.getCommandName())) {
      return null;
    }
    return supplier.apply(split[1]);
  }

  static CliCommandAction commandToActionNoCliArgumentsNullable(CliCommand info, String command, CliCommandAction target) {
    return command.equals(info.getCommandName()) ? target : null;
  }
}
