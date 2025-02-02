package utils;

import exceptions.LockException;
import java.util.Map;

public interface ILockTable {
  int allocate();

  boolean isDefined(int location);

  int getValue(int location) throws LockException;

  void update(int location, int value) throws LockException;

  Map<Integer, Integer> getLockTable();

  void setLockTable(Map<Integer, Integer> newLockTable);
}