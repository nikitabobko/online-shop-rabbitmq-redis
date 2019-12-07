package ru.bobko.shop.frontend.cli.command.base;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum HelpCliCommand implements CliCommand {
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
  public CliCommandAction commandToActionOrNull(String command) {
    return CliCommand.noCliArgumentCommandToActionOrNull(this, command, HelpCliCommandAction.INSTANCE);
  }

  public enum HelpCliCommandAction implements CliCommandAction {
    INSTANCE;

    @Override
    public void exec() {
      CliCommandAction.printResponse(
        Stream.concat(Stream.of("Available commands:"), CliCommand.registered.stream().map(CliCommand::getHelp))
          .collect(Collectors.joining("\n\n")));
    }
  }
}
