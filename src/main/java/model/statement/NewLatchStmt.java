package model.statement;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import model.value.IntValue;
import utils.IDict;
import utils.IHeap;
import utils.ILatchTable;

public class NewLatchStmt implements IStmt {
  private String var;
  private IExp exp;

  public NewLatchStmt(String var, IExp exp) {
    this.var = var;
    this.exp = exp;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    try {
      ILatchTable<Integer, Integer> latchTable = prg.getLatchTable();
      IValue num1 = this.exp.eval(prg.getSymTable(), prg.getHeap());
      if (!num1.getType().equals(new IntType()))
        throw new MyException("NewLatchStmt: Invalid type for expression");
      try {
        if (!prg.getSymTable().isDefined(this.var) || !prg.getSymTable().get(this.var).getType().equals(new IntType()))
          throw new MyException("NewLatchStmt: Invalid type for variable");
      } catch (DictionaryException e) {
        throw new MyException(e.getMessage());
      }
      Integer newLatch = latchTable.getFirstFreeLocation();
      latchTable.put(newLatch, ((IntValue) num1).getVal());
      try {
        prg.getSymTable().update(this.var, new IntValue(newLatch));
      } catch (DictionaryException e) {
        throw new MyException(e.getMessage());
      }
    } catch (ExpressionException | MyException e) {
      throw new MyException(e.getMessage());
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new NewLatchStmt(this.var, this.exp);
  }

  @Override
  public String toString() {
    return "NewLatchStmt(" + this.var + ", " + this.exp + ")";
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType varType = typeEnv.get(var);
      IType expType = exp.typecheck(typeEnv);

      if (!varType.equals(new IntType())) {
        throw new MyException("NewLatchStmt: Variable must be of type int");
      }
      if (!expType.equals(new IntType())) {
        throw new MyException("NewLatchStmt: Expression must evaluate to int");
      }
      return typeEnv;
    } catch (ExpressionException | DictionaryException e) {
      throw new MyException(e.getMessage());
    }
  }

}
