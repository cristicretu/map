package view.command;

import controller.Controller;
import model.statement.IStmt;

public class RunExample extends Command {
  private Controller controller;
  private boolean hasBeenExecuted;

  public RunExample(String key, IStmt stmt, Controller controller) {
    super(key, stmt.toString());
    this.controller = controller;
    this.hasBeenExecuted = false;
  }

  @Override
  public void execute() {
    if (hasBeenExecuted) {
      System.out.println("Program has already been executed!");
      return;
    }
    controller.executeOneStep();
    hasBeenExecuted = true;
  }

  public boolean hasBeenExecuted() {
    return hasBeenExecuted;
  }
}
