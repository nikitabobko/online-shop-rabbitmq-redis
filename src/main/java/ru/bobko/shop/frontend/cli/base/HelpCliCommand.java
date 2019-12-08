package ru.bobko.shop.frontend.cli.base;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HelpCliCommand implements CliCommand, CliCommandAction {
  INSTANCE;

  @Override
  public String getCommandName() {
    return "help";
  }

  @Override
  public String getDescription() {
    return "Prints this help page";
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
      return Stream.concat(Stream.of("Available commands:"), registered.stream().map(CliCommand::getHelp))
        .collect(Collectors.joining("\n\n"));
  }
}
