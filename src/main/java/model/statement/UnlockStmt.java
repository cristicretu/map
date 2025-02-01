package model.statement;

import exceptions.DictionaryException;
import exceptions.LockException;
import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.value.IValue;
import model.value.IntValue;
import utils.IDict;

public class UnlockStmt implements IStmt {
  private String var;

  public UnlockStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    if (!prg.getSymTable().isDefined(var)) {
      throw new MyException(var + " is not defined in the symbol table");
    }
    IValue foundIndex;
    try {
      foundIndex = prg.getSymTable().get(var);
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
    if (!(foundIndex instanceof IntValue)) {
      throw new MyException(var + " is not an integer");
    }
    int index = ((IntValue) foundIndex).getVal();
    if (prg.getLock().isDefined(index)) {
      try {
        if (prg.getLock().get(index) == prg.getId()) {
          prg.getLock().update(index, -1);
        }
      } catch (LockException e) {
        throw new MyException(e.getMessage());
      }
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new UnlockStmt(var);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    return typeEnv;
  }

}
