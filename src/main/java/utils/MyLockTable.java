package utils;

import exceptions.LockException;
import java.util.HashMap;
import java.util.Map;

public class MyLockTable implements ILockTable {
  private Map<Integer, Integer> lockTable;
  private int freeLocation;

  public MyLockTable() {
    this.lockTable = new HashMap<>();
    this.freeLocation = 0;
  }

  @Override
  public synchronized int allocate() {
    freeLocation++;
    lockTable.put(freeLocation, -1);
    return freeLocation;
  }

  @Override
  public synchronized boolean isDefined(int location) {
    return lockTable.containsKey(location);
  }

  @Override
  public synchronized int getValue(int location) throws LockException {
    if (!isDefined(location)) {
      throw new LockException("Lock location " + location + " is not defined!");
    }
    return lockTable.get(location);
  }

  @Override
  public synchronized void update(int location, int value) throws LockException {
    if (!isDefined(location)) {
      throw new LockException("Lock location " + location + " is not defined!");
    }
    lockTable.put(location, value);
  }

  @Override
  public synchronized Map<Integer, Integer> getLockTable() {
    return new HashMap<>(lockTable);
  }

  @Override
  public synchronized void setLockTable(Map<Integer, Integer> newLockTable) {
    this.lockTable = new HashMap<>(newLockTable);
  }

  @Override
  public synchronized String toString() {
    return lockTable.toString();
  }
}