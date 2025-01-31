package model.exp;

import exceptions.ExpressionException;
import exceptions.MyException;
import model.type.IType;
import model.type.IntType;
import model.value.IValue;
import utils.IDict;
import utils.IHeap;

public class MulExp implements IExp {
  private IExp e1;
  private IExp e2;

  public MulExp(IExp e1, IExp e2) {
    this.e1 = e1;
    this.e2 = e2;
  }

  @Override
  public IValue eval(IDict<String, IValue> symTable, IHeap<Integer, IValue> heap)
      throws MyException, ExpressionException {
    IValue val1 = this.e1.eval(symTable, heap);
    IValue val2 = this.e2.eval(symTable, heap);
    if (val1.getType().equals(new IntType()) && val2.getType().equals(new IntType())) {
      return new ArithExp('-', new ArithExp('*', e1, e2), new ArithExp('+', e2, e1)).eval(symTable, heap);
    }
    throw new ExpressionException("MulExp: operands must be of type int");
  }

  @Override
  public IType typecheck(IDict<String, IType> typeEnv) throws ExpressionException {
    IType type1 = this.e1.typecheck(typeEnv);
    IType type2 = this.e2.typecheck(typeEnv);
    if (type1.equals(new IntType()) && type2.equals(new IntType())) {
      return new IntType();
    }
    throw new ExpressionException("MulExp: operands must be of type int");
  }

  @Override
  public IExp deepCopy() {
    return new MulExp(this.e1.deepCopy(), this.e2.deepCopy());
  }

  @Override
  public String toString() {
    return "MUL(" + this.e1.toString() + ", " + this.e2.toString() + ")";
  }
}
