package utils;

import exceptions.LockException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class MyLockTable implements ILockTable {
  private Map<Integer, Integer> lockTable;
  private int freeLocation;
  private final ReentrantLock lock;

  public MyLockTable() {
    this.lockTable = new HashMap<>();
    this.freeLocation = 0;
    this.lock = new ReentrantLock();
  }

  @Override
  public int allocate() {
    lock.lock();
    try {
      freeLocation++;
      lockTable.put(freeLocation, -1);
      return freeLocation;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public boolean isDefined(int location) {
    lock.lock();
    try {
      return lockTable.containsKey(location);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int getValue(int location) throws LockException {
    lock.lock();
    try {
      if (!isDefined(location)) {
        throw new LockException("Lock location " + location + " is not defined!");
      }
      return lockTable.get(location);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void update(int location, int value) throws LockException {
    lock.lock();
    try {
      if (!isDefined(location)) {
        throw new LockException("Lock location " + location + " is not defined!");
      }
      lockTable.put(location, value);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public Map<Integer, Integer> getLockTable() {
    lock.lock();
    try {
      return new HashMap<>(lockTable);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public void setLockTable(Map<Integer, Integer> newLockTable) {
    lock.lock();
    try {
      this.lockTable = new HashMap<>(newLockTable);
    } finally {
      lock.unlock();
    }
  }

  @Override
  public String toString() {
    lock.lock();
    try {
      return lockTable.toString();
    } finally {
      lock.unlock();
    }
  }
}