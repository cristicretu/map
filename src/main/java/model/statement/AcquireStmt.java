package model.statement;

import exceptions.MyException;
import exceptions.DictionaryException;
import model.PrgState;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import model.value.IntValue;
import javafx.util.Pair;
import utils.IDict;
import java.util.List;

public class AcquireStmt implements IStmt {
  private final String var;

  public AcquireStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    var symTable = prg.getSymTable();
    var semaphoreTable = prg.getSemaphoreTable();

    try {
      if (!symTable.isDefined(var)) {
        throw new MyException("Variable not declared");
      }

      IValue value = symTable.get(var);
      if (!value.getType().equals(new IntType())) {
        throw new MyException("Variable must be of type int");
      }

      int foundIndex = ((IntValue) value).getVal();
      if (!semaphoreTable.containsKey(foundIndex)) {
        throw new MyException("Invalid semaphore index");
      }

      // var semaphoreValue = semaphoreTable.get(foundIndex);
      Pair<Integer, List<Integer>> semaphoreValue = semaphoreTable.get(foundIndex);
      int N1 = semaphoreValue.getKey();
      List<Integer> List1 = semaphoreValue.getValue();
      int NL = List1.size();

      if (N1 > NL) {
        if (!List1.contains(prg.getId())) {
          List1.add(prg.getId());
          semaphoreTable.update(foundIndex, new Pair<>(N1, List1));
        }
      } else {
        prg.getExeStack().push(this);
      }
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }

    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new AcquireStmt(var);
  }

  @Override
  public String toString() {
    return String.format("acquire(%s)", var);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType varType = typeEnv.get(var);
      if (varType.equals(new IntType())) {
        return typeEnv;
      } else {
        throw new MyException("Variable must be of type int");
      }
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }
  }
}