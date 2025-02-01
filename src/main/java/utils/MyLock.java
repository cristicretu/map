package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.LockException;

public class MyLock implements ILock<Integer, Integer> {
  private Map<Integer, Integer> lock;
  private int newFreeLock;

  public MyLock() {
    this.lock = new HashMap<>();
    this.newFreeLock = 1;
  }

  @Override
  public void put(Integer key, Integer value) {
    synchronized (lock) {
      lock.put(key, value);
    }
    newFreeLock++;
  }

  @Override
  public Integer get(Integer key) throws LockException {
    synchronized (lock) {
      return lock.get(key);
    }
  }

  @Override
  public boolean isDefined(Integer key) {
    synchronized (lock) {
      return lock.containsKey(key);
    }
  }

  @Override
  public void update(Integer key, Integer value) throws LockException {
    synchronized (lock) {
      lock.put(key, value);
    }
  }

  @Override
  public List<Integer> getValues() {
    synchronized (lock) {
      return new ArrayList<>(lock.values());
    }
  }

  public int getNewFreeLock() {
    synchronized (lock) {
      return newFreeLock;
    }
  }

  @Override
  public ILock<Integer, Integer> deepCopy() {
    synchronized (lock) {
      MyLock copy = new MyLock();
      copy.lock = new HashMap<>(lock);
      return copy;
    }
  }

}
