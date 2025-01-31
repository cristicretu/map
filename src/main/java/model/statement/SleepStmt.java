package model.statement;

import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import utils.IDict;

public class SleepStmt implements IStmt {
  private int number;

  public SleepStmt(int number) {
    this.number = number;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    if (number > 0) {
      prg.getExeStack().push(new SleepStmt(number - 1));
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new SleepStmt(this.number);
  }

  @Override
  public String toString() {
    return "sleep(" + this.number + ")";
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    return typeEnv;
  }

}
