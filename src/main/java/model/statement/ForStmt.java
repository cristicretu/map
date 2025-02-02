package model.statement;

import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.exp.RelExp;
import model.exp.VariableExp;
import model.type.BoolType;
import model.type.IType;
import model.type.IntType;
import utils.IDict;

public class ForStmt implements IStmt {
  private String var;
  private IExp exp1, exp2, exp3;
  private IStmt stmt;

  public ForStmt(String var, IExp exp1, IExp exp2, IExp exp3, IStmt stmt) {
    this.var = var;
    this.exp1 = exp1;
    this.exp2 = exp2;
    this.exp3 = exp3;
    this.stmt = stmt;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    IStmt converted = new CompStmt(new VarDeclStmt(var, new IntType()),
        new CompStmt(new AssignStmt(var, exp1),
            new WhileStmt(new RelExp(new VariableExp(var), exp2, "<"), new CompStmt(stmt, new AssignStmt(var, exp3)))));
    prg.getExeStack().push(converted);
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new ForStmt(var, exp1, exp2, exp3, stmt);
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IDict<String, IType> newTypeEnv = typeEnv.deepCopy();
      newTypeEnv.put(var, new IntType());

      IType typexp1 = exp1.typecheck(newTypeEnv);
      IType typexp2 = exp2.typecheck(newTypeEnv);
      IType typexp3 = exp3.typecheck(newTypeEnv);

      if (typexp1.equals(new IntType()) && typexp2.equals(new IntType()) && typexp3.equals(new IntType())) {
        stmt.typecheck(newTypeEnv);
        return typeEnv;
      } else {
        throw new MyException("For statement: initialization and step must be int,");
      }
    } catch (ExpressionException e) {
      throw new MyException(e.getMessage());
    }
  }

}
