package ru.bobko.shop.frontend.cli.command.base;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class NoSuchCliCommandAction implements CliCommandAction {
  private final String actualUserCommand;

  public NoSuchCliCommandAction(String actualUserCommand) {
    this.actualUserCommand = actualUserCommand;
  }

  @Override
  public void exec() {
    List<CliCommand> potentialCandidates = CliCommand.registered.stream()
      .filter(it -> it.getCommandName().contains(actualUserCommand))
      .collect(toList());
    if (potentialCandidates.size() == 1) {
      CliCommandAction.printResponse(potentialCandidates.get(0).getHelp());
    } else {
      HelpCliCommand.HelpCliCommandAction.INSTANCE.exec();
    }
  }
}
