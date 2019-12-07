package ru.bobko.shop.frontend.cli.command.base;

import ru.bobko.shop.frontend.cli.command.AddToCardCliCommand;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public interface CliCommand {
  List<CliCommand> registered = Arrays.asList(
    AddToCardCliCommand.INSTANCE,
    HelpCliCommand.INSTANCE
  );

  String getCommandName();

  String getDescription();

  String getCommandUsage();

  default String getHelp() {
    return String.format("%s - %s\nUSAGE:\n> %s", getCommandName(), getDescription(), getCommandUsage());
  }

  CliCommandAction commandToActionOrNull(String command);

  static CliCommandAction lineCommandToAction(String line) {
    String command = line.trim();
    return registered.stream()
      .map(it -> it.commandToActionOrNull(command))
      .filter(Objects::nonNull)
      .findFirst()
      .orElse(new NoSuchCliCommandAction(command));
  }

  static CliCommandAction oneCliArgumentCommandToActionOrNull(CliCommand info,
                                                              String command,
                                                              Function<String, CliCommandAction> supplier) {
    String[] split = command.split("\\s*");
    if (split.length != 2) {
      return null;
    }
    if (!split[0].equals(info.getCommandName())) {
      return null;
    }
    return supplier.apply(split[1]);
  }

  static CliCommandAction noCliArgumentCommandToActionOrNull(CliCommand info, String command, CliCommandAction target) {
    return command.equals(info.getCommandName()) ? target : null;
  }
}
