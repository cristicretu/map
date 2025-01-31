package model.statement;

import exceptions.DictionaryException;
import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import model.value.IntValue;
import utils.IDict;

public class CountDownStmt implements IStmt {
  private String var;

  public CountDownStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    // Pop the statement is handled by the interpreter

    // Check if var exists in SymTable
    if (!prg.getSymTable().isDefined(this.var)) {
      throw new MyException("CountDownStmt: Variable not found in SymTable");
    }

    // Get foundIndex from SymTable
    IValue foundIndex;
    try {
      foundIndex = prg.getSymTable().get(this.var);
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }

    if (!foundIndex.getType().equals(new IntType())) {
      throw new MyException("CountDownStmt: Variable must be of type int");
    }

    int index = ((IntValue) foundIndex).getVal();

    // Check if foundIndex exists in LatchTable and update it atomically
    synchronized (prg.getLatchTable()) {
      if (!prg.getLatchTable().isDefined(index)) {
        throw new MyException("CountDownStmt: Index not found in LatchTable");
      }

      int latchValue = prg.getLatchTable().get(index);
      if (latchValue > 0) {
        prg.getLatchTable().put(index, latchValue - 1);
      }

      // Write the program state ID to output
      prg.getOutput().add(new IntValue(prg.getId()));
    }

    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new CountDownStmt(this.var);
  }

  @Override
  public String toString() {
    return "countDown(" + this.var + ")";
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
      throw new MyException("CountDownStmt: Variable must be of type int");
    }
    return typeEnv;
  }
}