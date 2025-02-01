package model.statement;

import exceptions.DictionaryException;
import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.value.IntValue;
import utils.IDict;

public class NewLockStmt implements IStmt {
  private String var;

  public NewLockStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    int newLock = prg.getLock().getNewFreeLock();
    if (!prg.getSymTable().isDefined(var)) {
      prg.getSymTable().put(var, new IntValue(newLock));
    } else {
      try {
        prg.getSymTable().update(var, new IntValue(newLock));
      } catch (DictionaryException e) {
        throw new MyException(e.getMessage());
      }
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new NewLockStmt(var);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    return typeEnv;
  }

  public String toString() {
    return "newLock(" + var + ")";
  }

}
