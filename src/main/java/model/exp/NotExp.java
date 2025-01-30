package model.exp;

import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.BoolType;
import model.type.IType;
import model.value.BoolValue;
import model.value.IValue;
import utils.IDict;
import utils.IHeap;

public class NotExp implements IExp {
  private IExp expression;

  public NotExp(IExp expression) {
    this.expression = expression;
  }

  @Override
  public IValue eval(IDict<String, IValue> symTable, IHeap<Integer, IValue> heap)
      throws ExpressionException, MyException {
    IValue value = expression.eval(symTable, heap);
    if (!(value instanceof BoolValue)) {
      throw new ExpressionException("Expression is not of boolean type");
    }
    BoolValue boolValue = (BoolValue) value;
    return new BoolValue(!boolValue.getVal());
  }

  @Override
  public IType typecheck(IDict<String, IType> typeEnv) throws ExpressionException {
    IType type = expression.typecheck(typeEnv);
    if (type.equals(new BoolType())) {
      return new BoolType();
    }
    throw new ExpressionException("Expression is not of boolean type");
  }

  @Override
  public IExp deepCopy() {
    return new NotExp(expression.deepCopy());
  }

  @Override
  public String toString() {
    return "!(" + expression.toString() + ")";
  }
}