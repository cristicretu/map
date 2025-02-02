package model;

import utils.IStack;
import utils.IDict;
import utils.IHeap;
import utils.IList;
import utils.MyStack;
import utils.MyDict;
import utils.ProcTuple;
import model.statement.IStmt;
import model.value.IValue;
import model.value.RefValue;
import model.value.StringValue;

import java.io.BufferedReader;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import exceptions.MyException;
import exceptions.StackException;

public class PrgState {
  private static int nextId = 0;
  private final int id;
  private boolean isNotCompleted;

  public boolean isNotCompleted() {
    return isNotCompleted;
  }

  public void setNotCompleted(boolean isNotCompleted) {
    this.isNotCompleted = isNotCompleted;
  }

  private IStack<IStmt> exeStack;

  public IStack<IStmt> getExeStack() {
    return exeStack;
  }

  private IStack<IDict<String, IValue>> symTableStack;
  private IDict<String, ProcTuple<List<String>, IStmt>> procTable;

  public IStack<IDict<String, IValue>> getSymTableStack() {
    return symTableStack;
  }

  public IDict<String, IValue> getSymTable() {
    try {
      return symTableStack.top();
    } catch (StackException e) {
      return null;
    }
  }

  public IDict<String, ProcTuple<List<String>, IStmt>> getProcTable() {
    return procTable;
  }

  public void setProcTable(IDict<String, ProcTuple<List<String>, IStmt>> procTable) {
    this.procTable = procTable;
  }

  private IList<IValue> output;

  public IList<IValue> getOutput() {
    return output;
  }

  private IStmt originalProgram;

  public IStmt getOriginalProgram() {
    return originalProgram;
  }

  private IDict<StringValue, BufferedReader> fileTable;

  public IDict<StringValue, BufferedReader> getFileTable() {
    return fileTable;
  }

  private IHeap<Integer, IValue> heap;

  public IHeap<Integer, IValue> getHeap() {
    return heap;
  }

  private static synchronized int getNextId() {
    return nextId++;
  }

  public PrgState(IStack<IStmt> exeStack, IDict<String, IValue> symTable, IList<IValue> output, IStmt originalProgram,
      IDict<StringValue, BufferedReader> fileTable, IHeap<Integer, IValue> heap) {
    this.id = getNextId();
    this.exeStack = exeStack;
    this.symTableStack = new MyStack<>();
    this.symTableStack.push(symTable);
    this.output = output;
    this.originalProgram = originalProgram.deepCopy();
    this.fileTable = fileTable;
    this.heap = heap;
    this.procTable = new MyDict<>();
    this.isNotCompleted = true;
    exeStack.push(originalProgram);
  }

  @Override
  public String toString() {
    return "PrgState{\n" + "id=" + id + ",\n exeStack=" + exeStack.getList() + ",\n symTableStack=" + symTableStack
        + ",\n output=" + output
        + ",\n originalProgram="
        + originalProgram + ",\n fileTable=" + fileTable + ",\n heap=" + heap + ",\n procTable=" + procTable + "\n}";
  }

  public Set<Integer> getUsedAddresses() {
    Set<Integer> usedAddresses = new HashSet<>();
    for (IValue value : this.getSymTable().getValues()) {
      if (value instanceof RefValue) {
        usedAddresses.add(((RefValue) value).getAddress());
      }
    }

    for (IValue value : this.heap.getValues()) {
      if (value instanceof RefValue) {
        usedAddresses.add(((RefValue) value).getAddress());
      }
    }

    return usedAddresses;
  }

  public PrgState oneStep() throws MyException {
    if (exeStack.isEmpty()) {
      this.isNotCompleted = false;
      return null;
    }

    IStmt crtStmt;
    try {
      crtStmt = exeStack.pop();
    } catch (StackException e) {
      throw new MyException("prgstate stack is empty2");
    }
    return crtStmt.execute(this);
  }

  public int getId() {
    return id;
  }
}
