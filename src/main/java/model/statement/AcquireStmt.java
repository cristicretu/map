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

public class AcquireStmt implements IStmt {
  private String var;

  public AcquireStmt(String var) {
    this.var = var;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    var symTable = prg.getSymTable();
    var semTable = prg.getSemaphore();

    if (!symTable.isDefined(var)) {
      throw new MyException("AcquireStmt: " + var + " not defined in symbol table");
    }

    try {
      var value = symTable.get(var);
      if (!(value instanceof IntValue)) {
        throw new MyException("AcquireStmt: " + var + " is not of type int");
      }

      int foundIndex = ((IntValue) value).getVal();
      if (!semTable.containsKey(foundIndex)) {
        throw new MyException("AcquireStmt: invalid semaphore index " + foundIndex);
      }

      Tuple<Integer, List<Integer>, Integer> entry = semTable.get(foundIndex);
      int N1 = entry.getFirst(); // Number of permits
      List<Integer> List1 = entry.getSecond(); // List of threads holding the semaphore
      int N2 = entry.getThird(); // Initial number of permits (not used in acquire)
      int NL = List1.size();

      if (N1 > NL) { // If there are available permits
        if (!List1.contains(prg.getId())) { // If thread not already in list
          List1.add(prg.getId());
          semTable.update(foundIndex, new Tuple<>(N1, List1, N2));
        }
      } else {
        prg.getExeStack().push(this); // Try again later
      }
    } catch (DictionaryException e) {
      throw new MyException("AcquireStmt: " + e.getMessage());
    }

    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new AcquireStmt(var);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typevar = typeEnv.get(var);
      if (!typevar.equals(new IntType())) {
        throw new MyException("AcquireStmt: " + var + " is not of type int");
      }
      return typeEnv;
    } catch (DictionaryException e) {
      throw new MyException("AcquireStmt: " + e.getMessage());
    }
  }

  @Override
  public String toString() {
    return "acquire(" + var + ")";
  }
}