package model.statement;

import exceptions.MyException;
import exceptions.DictionaryException;
import model.PrgState;
import model.type.IType;
import model.value.IntValue;
import utils.IDict;

public class NewLockStmt implements IStmt {
  private final String var;

  public NewLockStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState state) throws MyException {
    var lockTable = state.getLockTable();
    var symTable = state.getSymTable();

    int newFreeLocation = lockTable.allocate();
    try {
      lockTable.update(newFreeLocation, -1);

      if (!symTable.isDefined(var)) {
        throw new MyException("Variable " + var + " is not defined in the symbol table");
      }

      if (!symTable.get(var).getType().equals(new IntValue(0).getType())) {
        throw new MyException("Variable " + var + " is not of type int");
      }

      symTable.update(var, new IntValue(newFreeLocation));
    } catch (DictionaryException e) {
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
    return new NewLockStmt(var);
  }

  @Override
  public String toString() {
    return "newLock(" + var + ")";
  }
}