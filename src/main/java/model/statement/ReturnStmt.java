package model.statement;

import exceptions.MyException;
import exceptions.StackException;
import model.PrgState;
import model.type.IType;
import utils.IDict;

public class ReturnStmt implements IStmt {
  @Override
  public PrgState execute(PrgState prg) throws MyException {
    try {
      // Pop the current symbol table from the stack
      prg.getSymTableStack().pop();
    } catch (StackException e) {
      throw new MyException("Return statement error: " + e.getMessage());
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new ReturnStmt();
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    return typeEnv;
  }

  @Override
  public String toString() {
    return "return";
  }
}