package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exceptions.LatchException;

public class MyLatchTable implements ILatchTable<Integer, Integer> {
  private Map<Integer, Integer> latchTable;
  private int nextFreeLocation;

  public MyLatchTable() {
    this.latchTable = new HashMap<>();
    this.nextFreeLocation = 1;
  }

  @Override
  public int get(int position) throws LatchException {
    synchronized (this) {
      if (!this.getLatchTable().containsKey(position))
        throw new LatchException(String.format("%d is not present in the table!", position));
      return this.getLatchTable().get(position);
    }
  }

  @Override
  public void put(Integer key, Integer value) throws LatchException {
    if (!key.equals(nextFreeLocation))
      throw new LatchException("Invalid lock table location!");
    this.latchTable.put(key, value);
    synchronized (this) {
      nextFreeLocation++;
    }
  }

  public int put(Integer value) throws LatchException {
    this.latchTable.put(nextFreeLocation, value);
    synchronized (this) {
      nextFreeLocation++;
    }
    return nextFreeLocation - 1;
  }

  @Override
  public void remove(Integer key) {
    this.latchTable.remove(key);
  }

  @Override
  public boolean isDefined(Integer key) {
    return this.latchTable.containsKey(key);
  }

  @Override
  public Map<Integer, Integer> getLatchTable() {
    return this.latchTable;
  }

  @Override
  public List<Integer> getValues() {
    return new ArrayList<>(this.latchTable.values());
  }

  @Override
  public void setLatchTable(Map<Integer, Integer> dictionary) {
    this.latchTable = dictionary;
  }

  @Override
  public int getFirstFreeLocation() {
    int locationAddress = 1;
    while (this.getLatchTable().get(locationAddress) != null)
      locationAddress++;
    return locationAddress;
  }

  @Override
  public String toString() {
    StringBuilder answer = new StringBuilder();
    for (Integer key : this.getLatchTable().keySet()) {
      answer.append(key.toString());
      answer.append(" -> ");
      answer.append(this.getLatchTable().get(key).toString());
      answer.append("\n");
    }
    return answer.toString();
  }

}
