package model.statement;

import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.exp.RelExp;
import model.type.IType;
import utils.IDict;

public class SwitchStmt implements IStmt {
  private IExp exp, exp1, exp2;
  private IStmt stmt1, stmt2, stmt3;

  public SwitchStmt(IExp exp, IExp exp1, IExp exp2, IStmt stmt1, IStmt stmt2, IStmt stmt3) {
    this.exp = exp;
    this.exp1 = exp1;
    this.exp2 = exp2;
    this.stmt1 = stmt1;
    this.stmt2 = stmt2;
    this.stmt3 = stmt3;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    IStmt converted = new IfStmt(new RelExp(exp, exp1, "=="), stmt1,
        new IfStmt(new RelExp(exp, exp2, "=="), stmt2, stmt3));
    prg.getExeStack().push(converted);
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new SwitchStmt(exp.deepCopy(), exp1.deepCopy(), exp2.deepCopy(), stmt1.deepCopy(), stmt2.deepCopy(),
        stmt3.deepCopy());
  }

  @Override
  public String toString() {
    return "switch(" + exp + ") (case " + exp1 + ": " + stmt1 + ") (case " + exp2 + ": " + stmt2 + ") (default: "
        + stmt3 + ")";
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typexp = exp.typecheck(typeEnv);
      IType typexp1 = exp1.typecheck(typeEnv);
      IType typexp2 = exp2.typecheck(typeEnv);
      if (!typexp.equals(typexp1) || !typexp.equals(typexp2)) {
        throw new MyException("Switch statement: all expressions must have the same type");
      }

      stmt1.typecheck(typeEnv);
      stmt2.typecheck(typeEnv);
      stmt3.typecheck(typeEnv);
    } catch (ExpressionException e) {
      throw new MyException(e.getMessage());
    }
    return typeEnv;
  }

}
