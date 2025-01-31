package model.statement;

import exceptions.MyException;
import model.PrgState;
import model.exp.ConstantValue;
import model.type.IType;
import model.value.IntValue;
import utils.IDict;

public class WaitStmt implements IStmt {
  private int time;

  public WaitStmt(int time) {
    this.time = time;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    if (time > 0) {
      WaitStmt waitStmt = new WaitStmt(time - 1);
      PrintStmt printStmt = new PrintStmt(new ConstantValue(new IntValue(time)));
      CompStmt compStmt = new CompStmt(printStmt, waitStmt);
      prg.getExeStack().push(compStmt);
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new WaitStmt(time);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    return typeEnv;
  }

  @Override
  public String toString() {
    return "wait(" + time + ")";
  }
}
