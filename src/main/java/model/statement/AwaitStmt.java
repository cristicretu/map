package model.statement;

import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import utils.IDict;

public class AwaitStmt implements IStmt {
  private String var;

  public AwaitStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    if (!prg.getSymTable().isDefined(this.var)) {
      throw new MyException("AwaitStmt: Variable not found");
    }
    IValue foundIndex = prg.getSymTable().get(this.var);
    if (!foundIndex.getType().equals(new IntType())) {
      throw new MyException("AwaitStmt: Invalid type for variable");
    }
    if (!prg.getLatchTable().isDefined(((IntValue) foundIndex).getVal())) {
      throw new MyException("AwaitStmt: Latch not found");
    }
    if (prg.getLatchTable().get(((IntValue) foundIndex).getVal()) == 0) {
      throw new MyException("AwaitStmt: Latch not found");
    }

    prg.getExeStack().push(this);
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new AwaitStmt(this.var);
  }

  @Override
  public String toString() {
    return "await(" + this.var + ")";
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    return typeEnv;
  }
}
