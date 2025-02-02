package model.statement;

import java.util.List;

import exceptions.DictionaryException;
import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.type.IntType;
import model.value.IntValue;
import utils.IDict;
import utils.Tuple;

public class ReleaseStmt implements IStmt {
  private String var;

  public ReleaseStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    var symTable = prg.getSymTable();
    var semTable = prg.getSemaphore();

    if (!symTable.isDefined(var)) {
      throw new MyException("ReleaseStmt: " + var + " not defined in symbol table");
    }

    try {
      var value = symTable.get(var);
      if (!(value instanceof IntValue)) {
        throw new MyException("ReleaseStmt: " + var + " is not of type int");
      }

      int foundIndex = ((IntValue) value).getVal();
      if (!semTable.containsKey(foundIndex)) {
        throw new MyException("ReleaseStmt: invalid semaphore index " + foundIndex);
      }

      Tuple<Integer, List<Integer>, Integer> entry = semTable.get(foundIndex);
      int N1 = entry.getFirst();
      List<Integer> List1 = entry.getSecond();
      int N2 = entry.getThird();

      if (List1.contains(prg.getId())) {
        List1.remove(Integer.valueOf(prg.getId()));
        semTable.update(foundIndex, new Tuple<>(N1, List1, N2));
      }
    } catch (DictionaryException e) {
      throw new MyException("ReleaseStmt: " + e.getMessage());
    }

    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new ReleaseStmt(var);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typevar = typeEnv.get(var);
      if (!typevar.equals(new IntType())) {
        throw new MyException("ReleaseStmt: " + var + " is not of type int");
      }
      return typeEnv;
    } catch (DictionaryException e) {
      throw new MyException("ReleaseStmt: " + e.getMessage());
    }
  }

  @Override
  public String toString() {
    return "release(" + var + ")";
  }
}