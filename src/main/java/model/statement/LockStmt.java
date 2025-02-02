package model.statement;

import exceptions.MyException;
import exceptions.DictionaryException;
import exceptions.LockException;
import model.PrgState;
import model.type.IType;
import model.value.IntValue;
import utils.IDict;

public class LockStmt implements IStmt {
  private final String var;

  public LockStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState state) throws MyException {
    var lockTable = state.getLockTable();
    var symTable = state.getSymTable();

    try {
      if (!symTable.isDefined(var)) {
        throw new MyException("Variable " + var + " is not defined in the symbol table");
      }

      if (!symTable.get(var).getType().equals(new IntValue(0).getType())) {
        throw new MyException("Variable " + var + " is not of type int");
      }

      IntValue fi = (IntValue) symTable.get(var);
      int foundIndex = fi.getVal();

      if (!lockTable.isDefined(foundIndex)) {
        throw new MyException("Lock location " + foundIndex + " is not defined in the lock table");
      }

      if (lockTable.getValue(foundIndex) == -1) {
        lockTable.update(foundIndex, state.getId());
      } else {
        state.getExeStack().push(this);
      }

    } catch (DictionaryException | LockException e) {
      throw new MyException(e.getMessage());
    }

    return null;
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      if (!typeEnv.isDefined(var)) {
        throw new MyException("Variable " + var + " is not defined");
      }

      if (!typeEnv.get(var).equals(new IntValue(0).getType())) {
        throw new MyException("Variable " + var + " is not of type int");
      }

      return typeEnv;
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
  }

  @Override
  public IStmt deepCopy() {
    return new LockStmt(var);
  }

  @Override
  public String toString() {
    return "lock(" + var + ")";
  }
}