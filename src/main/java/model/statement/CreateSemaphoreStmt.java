package model.statement;

import exceptions.MyException;
import exceptions.DictionaryException;
import exceptions.ExpressionException;
import model.PrgState;
import model.exp.IExp;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import model.value.IntValue;
import javafx.util.Pair;
import utils.IDict;
import java.util.ArrayList;

public class CreateSemaphoreStmt implements IStmt {
  private final String var;
  private final IExp exp;

  public CreateSemaphoreStmt(String var, IExp exp) {
    this.var = var;
    this.exp = exp;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    var symTable = prg.getSymTable();
    var semaphoreTable = prg.getSemaphoreTable();

    try {
      IValue expValue = exp.eval(symTable, prg.getHeap());
      if (!expValue.getType().equals(new IntType())) {
        throw new MyException("Expression must evaluate to an integer");
      }

      int number1 = ((IntValue) expValue).getVal();
      int freeLocation = semaphoreTable.getFreeLocation();

      semaphoreTable.put(freeLocation, new Pair<>(number1, new ArrayList<>()));

      if (symTable.isDefined(var)) {
        try {
          if (!symTable.get(var).getType().equals(new IntType())) {
            throw new MyException("Variable must be of type int");
          }
          symTable.update(var, new IntValue(freeLocation));
        } catch (DictionaryException e) {
          throw new MyException(e.getMessage());
        }
      } else {
        throw new MyException("Variable not declared");
      }
    } catch (ExpressionException e) {
      throw new MyException(e.getMessage());
    }

    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new CreateSemaphoreStmt(var, exp.deepCopy());
  }

  @Override
  public String toString() {
    return String.format("createSemaphore(%s, %s)", var, exp);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typevar = typeEnv.get(var);
      IType typexp = exp.typecheck(typeEnv);
      if (typevar.equals(new IntType())) {
        if (typexp.equals(new IntType())) {
          return typeEnv;
        } else {
          throw new MyException("Expression type must be int");
        }
      } else {
        throw new MyException("Variable type must be int");
      }
    } catch (DictionaryException | ExpressionException e) {
      throw new MyException(e.getMessage());
    }
  }
}