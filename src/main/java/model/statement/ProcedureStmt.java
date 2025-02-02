package model.statement;

import java.util.List;

import exceptions.MyException;
import model.PrgState;
import model.type.IType;
import model.type.IntType;
import utils.IDict;
import utils.MyDict;
import utils.ProcTuple;

public class ProcedureStmt implements IStmt {
  private String procName;
  private List<String> params;
  private IStmt body;

  public ProcedureStmt(String procName, List<String> params, IStmt body) {
    this.procName = procName;
    this.params = params;
    this.body = body;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    // Add procedure to procedure table
    // Note: We allow redefinition of procedures for the initialization phase
    prg.getProcTable().put(procName, new ProcTuple<>(params, body.deepCopy()));
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new ProcedureStmt(procName, List.copyOf(params), body.deepCopy());
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    // Create a new type environment for the procedure body
    IDict<String, IType> procEnv = typeEnv.deepCopy();

    // Add all parameters as IntType (since in the example they're all integers)
    for (String param : params) {
      procEnv.put(param, new IntType());
    }

    // Typecheck the body with the new environment
    body.typecheck(procEnv);

    return typeEnv;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("procedure ").append(procName).append("(");
    for (int i = 0; i < params.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(params.get(i));
    }
    sb.append(") ").append(body.toString());
    return sb.toString();
  }
}