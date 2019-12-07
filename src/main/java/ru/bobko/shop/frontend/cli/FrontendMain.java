package ru.bobko.shop.frontend.cli;

import ru.bobko.shop.frontend.cli.command.base.CliCommand;
import ru.bobko.shop.frontend.cli.command.base.CliCommandAction;
import ru.bobko.shop.util.StringUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FrontendMain {
  public static void main(String[] args) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    while (true) {
      System.out.print("> ");
      String line = reader.readLine();
      if (StringUtil.isEmpty(line)) {
        break;
      }
      CliCommandAction command = CliCommand.lineCommandToAction(line);
      command.exec();
    }
  }
}
