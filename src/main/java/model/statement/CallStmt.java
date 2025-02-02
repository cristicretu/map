package model.statement;

import java.util.ArrayList;
import java.util.List;

import exceptions.DictionaryException;
import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import utils.IDict;
import utils.MyDict;
import utils.ProcTuple;

public class CallStmt implements IStmt {
  private String procName;
  private List<IExp> args;

  public CallStmt(String procName, List<IExp> args) {
    this.procName = procName;
    this.args = args;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    IDict<String, ProcTuple<List<String>, IStmt>> procTable = prg.getProcTable();

    if (!procTable.isDefined(procName)) {
      throw new MyException("Procedure " + procName + " is not defined");
    }

    ProcTuple<List<String>, IStmt> procDef;
    try {
      procDef = procTable.get(procName);
    } catch (DictionaryException e) {
      throw new MyException(e.getMessage());
    }

    List<String> params = procDef.getFirst();
    IStmt body = procDef.getSecond();

    if (params.size() != args.size()) {
      throw new MyException("Number of arguments does not match number of parameters for procedure " + procName);
    }

    IDict<String, IValue> newSymTable = new MyDict<>();

    // Evaluate arguments and bind them to parameters
    for (int i = 0; i < params.size(); i++) {
      try {
        IValue value = args.get(i).eval(prg.getSymTable(), prg.getHeap());
        newSymTable.put(params.get(i), value);
      } catch (ExpressionException | MyException e) {
        throw new MyException("Error evaluating argument " + (i + 1) + ": " + e.getMessage());
      }
    }

    // Push the new symbol table onto the stack
    prg.getSymTableStack().push(newSymTable);

    // Push return statement and procedure body onto execution stack
    prg.getExeStack().push(new ReturnStmt());
    prg.getExeStack().push(body.deepCopy()); // Use deepCopy to avoid sharing state

    return null;
  }

  @Override
  public IStmt deepCopy() {
    List<IExp> newArgs = new ArrayList<>();
    for (IExp exp : args) {
      newArgs.add(exp.deepCopy());
    }
    return new CallStmt(procName, newArgs);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    // Check that all arguments are of type int (since in our example all parameters
    // are ints)
    for (IExp exp : args) {
      try {
        IType expType = exp.typecheck(typeEnv);
        if (!expType.equals(new IntType())) {
          throw new MyException("CallStmt: argument " + exp + " is not of type int");
        }
      } catch (ExpressionException e) {
        throw new MyException("CallStmt: " + e.getMessage());
      }
    }
    return typeEnv;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("call ").append(procName).append("(");
    for (int i = 0; i < args.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(args.get(i));
    }
    sb.append(")");
    return sb.toString();
  }
}