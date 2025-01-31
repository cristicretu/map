package model.statement;

import exceptions.DictionaryException;
import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import model.value.IntValue;
import utils.IDict;

public class AwaitStmt implements IStmt {
  private String var;

  public AwaitStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    // Check if var exists in SymTable
    if (!prg.getSymTable().isDefined(this.var)) {
      throw new MyException("AwaitStmt: Variable not found in SymTable");
    }

    // Get foundIndex from SymTable
    IValue foundIndex;
    try {
      foundIndex = prg.getSymTable().get(this.var);
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
    if (!foundIndex.getType().equals(new IntType())) {
      throw new MyException("AwaitStmt: Variable must be of type int");
    }

    int index = ((IntValue) foundIndex).getVal();

    // Check if foundIndex exists in LatchTable
    synchronized (prg.getLatchTable()) {
      if (!prg.getLatchTable().isDefined(index)) {
        throw new MyException("AwaitStmt: Index not found in LatchTable");
      }

      // If LatchTable[foundIndex] != 0, push back await statement
      if (prg.getLatchTable().get(index) != 0) {
        prg.getExeStack().push(this);
      }
    }

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
    IType varType;
    try {
      varType = typeEnv.get(var);
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
    if (!varType.equals(new IntType())) {
      throw new MyException("AwaitStmt: Variable must be of type int");
    }
    return typeEnv;
  }
}
