
package model.statement;

import exceptions.ExpressionException;
import exceptions.MyException;
import model.PrgState;
import model.exp.IExp;
import model.exp.NotExp;
import model.type.BoolType;
import model.type.IType;
import utils.IDict;

public class RepeatUntilStmt implements IStmt {
  private IExp expression;
  private IStmt statement;

  public RepeatUntilStmt(IExp expression, IStmt statement) {
    this.statement = statement;
    this.expression = expression;
  }

  @Override
  public PrgState execute(PrgState prg) throws MyException {
    IStmt newStatement = new CompStmt(
        statement,
        new WhileStmt(
            new NotExp(expression),
            statement));

    prg.getExeStack().push(newStatement);
    return null;
  }

  @Override
  public IStmt deepCopy() {
    return new RepeatUntilStmt(expression.deepCopy(), statement.deepCopy());
  }

  @Override
  public String toString() {
    return "repeat{" + statement.toString() + "} until (" + expression.toString() + ")";
  }

  @Override
  public IDict<String, IType> typecheck(IDict<String, IType> typeEnv) throws MyException {
    try {
      IType typexp = expression.typecheck(typeEnv);
      if (typexp.equals(new BoolType())) {
        statement.typecheck(typeEnv.deepCopy());
        return typeEnv;
      } else {
        throw new MyException("The condition of REPEAT UNTIL has not the type bool");
      }
    } catch (ExpressionException e) {
      throw new MyException(e.getMessage());
    }
  }

}
