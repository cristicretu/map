package model.statement;

import java.util.List;

import exceptions.CyclicBarrierException;
import exceptions.DictionaryException;
import exceptions.MyException;
import javafx.util.Pair;
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
    IValue foundIndex;
    try {
      foundIndex = prg.getSymTable().get(var);
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
    if (!foundIndex.getType().equals(new IntType())) {
      throw new MyException("AwaitStmt: variable " + var + " is not an integer");
    }
    int index = ((IntValue) foundIndex).getVal();
    if (!prg.getCyclicBarrier().isDefined(index)) {
      throw new MyException("AwaitStmt: variable " + var + " is not a valid index");
    }
    Pair<Integer, List<Integer>> entry;
    try {
      entry = prg.getCyclicBarrier().get(index);
    } catch (CyclicBarrierException e) {
      throw new MyException(e.getMessage());
    }
    var n1 = entry.getKey();
    var list1 = entry.getValue();

    int nl = list1.size();
    if (n1 > nl) {
      if (list1.contains(prg.getId())) {
        prg.getExeStack().push(this);
      } else {
        list1.add(prg.getId());
        prg.getExeStack().push(this);
      }
    }
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new AwaitStmt(var);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      if (!typeEnv.get(var).equals(new IntType())) {
        throw new MyException("AwaitStmt: variable " + var + " is not an integer");
      }
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
    return typeEnv;
  }

  @Override
  public String toString() {
    return "await(" + var + ")";
  }

}
