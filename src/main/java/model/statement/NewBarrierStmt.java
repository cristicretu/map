package model.statement;

import java.util.ArrayList;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import javafx.util.Pair;
import model.PrgState;
import model.exp.IExp;
import model.type.IType;
import model.type.IntType;
import model.value.IntValue;
import model.value.IValue;
import utils.IDict;

public class NewBarrierStmt implements IStmt {
  private String var;
  private IExp exp;

  public NewBarrierStmt(String var, IExp exp) {
    this.var = var;
    this.exp = exp;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    try {
      IValue value = exp.eval(prg.getSymTable(), prg.getHeap());
      if (!(value instanceof IntValue)) {
        throw new MyException("The expression is not an integer");
      }
      IntValue nr = (IntValue) value;
      try {
        if (!prg.getSymTable().isDefined(var)) {
          throw new MyException(var + " is not defined in the symbol table");
        }
        if (!prg.getSymTable().get(var).getType().equals(new IntType())) {
          throw new MyException(var + " is not of type int");
        }
      } catch (DictionaryException e) {
        throw new MyException(e.getMessage());
      }
      int nextFreeLocation = prg.getCyclicBarrier().getNextFreeLocation();
      prg.getCyclicBarrier().put(nextFreeLocation, new Pair<>(nr.getVal(), new ArrayList<>()));
      try {
        prg.getSymTable().update(var, new IntValue(nextFreeLocation));
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
    return new NewBarrierStmt(var, exp.deepCopy());
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typevar = typeEnv.get(var);
      IType typexp = exp.typecheck(typeEnv);
      if (typevar.equals(new IntType()) && typexp.equals(new IntType())) {
        return typeEnv;
      }
      throw new MyException("NEWBARRIER stmt: right hand side and left hand side have different types");
    } catch (MyException | ExpressionException | DictionaryException e) {
      throw new MyException(e.getMessage());
    }
  }

  @Override
  public String toString() {
    return "newBarrier(" + var + ", " + exp + ")";
  }

}
