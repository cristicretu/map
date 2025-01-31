
package model.statement;

import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.type.BoolType;
import model.type.IType;
import utils.IDict;

public class TernaryOpStmt implements IStmt {
  private String var;
  private IExp exp1, exp2, exp3;

  public TernaryOpStmt(String var, IExp exp1, IExp exp2, IExp exp3) {
    this.var = var;
    this.exp1 = exp1;
    this.exp2 = exp2;
    this.exp3 = exp3;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    IfStmt converted = new IfStmt(exp1, new AssignStmt(var, exp2), new AssignStmt(var, exp3));
    prg.getExeStack().push(converted);
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new TernaryOpStmt(var, exp1, exp2, exp3);
  }

  @Override
  public String toString() {
    return "(" + exp1.toString() + ") ? " + exp2.toString() + " : " + exp3.toString();
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typexp = exp1.typecheck(typeEnv);
      if (typexp.equals(new BoolType())) {
        exp2.typecheck(typeEnv.deepCopy());
        exp3.typecheck(typeEnv.deepCopy());
        return typeEnv;
      } else {
        throw new MyException("The condition of the cond assignment has not the type bool");
      }

    } catch (ExpressionException e) {
      throw new MyException(e.getMessage());
    }
  }

}
