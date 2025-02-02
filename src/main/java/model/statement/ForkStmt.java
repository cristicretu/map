package model.statement;

import exceptions.MyException;
import exceptions.StackException;
import exceptions.DictionaryException;
import model.PrgState;
import model.type.IType;
import model.value.IValue;
import utils.IDict;
import utils.IStack;
import utils.MyStack;
import utils.ProcTuple;

import java.util.ArrayList;
import java.util.List;

public class ForkStmt implements IStmt {
  private IStmt statement;

  public ForkStmt(IStmt statement) {
    this.statement = statement;
  }

  @Override
  public PrgState execute(PrgState currentPrg) throws MyException {
    // Create a new stack for the forked process
    IStack<IDict<String, IValue>> newSymTableStack = new MyStack<>();

    // Clone the entire stack of symbol tables
    List<IDict<String, IValue>> tables = new ArrayList<>();
    IStack<IDict<String, IValue>> currentStack = currentPrg.getSymTableStack();

    while (!currentStack.isEmpty()) {
      try {
        tables.add(0, currentStack.pop().deepCopy());
      } catch (StackException e) {
        throw new MyException("Fork error: " + e.getMessage());
      }
    }

    // Restore the original stack
    for (IDict<String, IValue> table : tables) {
      currentStack.push(table);
      newSymTableStack.push(table.deepCopy());
    }

    try {
      PrgState forkedState = new PrgState(
          new MyStack<>(),
          newSymTableStack.top(), // Pass the top symbol table to constructor
          currentPrg.getOutput(),
          statement,
          currentPrg.getFileTable(),
          currentPrg.getHeap());

      // Copy the procedure table to the forked state by using deepCopy
      IDict<String, ProcTuple<List<String>, IStmt>> sourceProcTable = currentPrg.getProcTable();
      forkedState.setProcTable(sourceProcTable.deepCopy());

      return forkedState;
    } catch (StackException e) {
      throw new MyException("Fork error: " + e.getMessage());
    }
  }

  @Override
  public IStmt deepCopy() {
    return new ForkStmt(statement.deepCopy());
  }

  @Override
  public String toString() {
    return "fork(" + statement.toString() + ")";
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    statement.typecheck(typeEnv.deepCopy());
    return typeEnv;
  }
}