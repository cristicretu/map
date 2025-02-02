package model.statement;

import java.util.ArrayList;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import utils.IDict;
import utils.Tuple;

public class NewSemStmt implements IStmt {
  private String varName;
  private IExp exp1, exp2;

  public NewSemStmt(String varName, IExp exp1, IExp exp2) {
    this.varName = varName;
    this.exp1 = exp1;
    this.exp2 = exp2;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    var symTable = prg.getSymTable();
    var semTable = prg.getSemaphore();

    // First evaluate exp1 and exp2 using SymTable1 and Heap1
    IValue number1;
    try {
      number1 = exp1.eval(symTable, prg.getHeap());
    } catch (ExpressionException | MyException e) {
      throw new MyException("NewSemStmt: " + e.getMessage());
    }
    IValue number2;
    try {
      number2 = exp2.eval(symTable, prg.getHeap());
    } catch (ExpressionException | MyException e) {
      throw new MyException("NewSemStmt: " + e.getMessage());
    }

    if (!(number1.getType().equals(new IntType()) && number2.getType().equals(new IntType()))) {
      throw new MyException("NewSemStmt: expressions must evaluate to int");
    }

    int val1 = ((model.value.IntValue) number1).getVal();
    int val2 = ((model.value.IntValue) number2).getVal();

    // Check if var exists in SymTable1 and has type int
    if (!symTable.isDefined(varName)) {
      throw new MyException("NewSemStmt: " + varName + " not defined in symbol table");
    }
    try {
      if (!symTable.get(varName).getType().equals(new IntType())) {
        throw new MyException("NewSemStmt: " + varName + " is not of type int");
      }
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }

    // Get new location and update SemaphoreTable (synchronized through MySem
    // implementation)
    int newFreeLocation = semTable.getFreeLocation();
    semTable.put(newFreeLocation, new Tuple<>(val1, new ArrayList<>(), val2));

    // Update SymTable with the new location
    symTable.put(varName, new model.value.IntValue(newFreeLocation));

    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new NewSemStmt(varName, exp1.deepCopy(), exp2.deepCopy());
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typevar = typeEnv.get(varName);
      IType typeexp1 = exp1.typecheck(typeEnv);
      IType typeexp2 = exp2.typecheck(typeEnv);
      if (!typevar.equals(new IntType())) {
        throw new MyException("NewSemStmt: " + varName + " must be of type int");
      }
      if (!typeexp1.equals(new IntType()) || !typeexp2.equals(new IntType())) {
        throw new MyException("NewSemStmt: expressions must evaluate to int");
      }
    } catch (DictionaryException | ExpressionException e) {
      throw new MyException("NewSemStmt: " + varName + " not found in type environment");
    }
    return typeEnv;
  }

  @Override
  public String toString() {
    return "newSem(" + varName + ", " + exp1.toString() + ", " + exp2.toString() + ")";
  }

}
