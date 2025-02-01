package utils;

import java.util.List;

import exceptions.CyclicBarrierException;
import exceptions.DictionaryException;
import javafx.util.Pair;

public class MyCyclicBarrier implements ICyclicBarrier {
  private int nextFreeLocation;
  private IDict<Integer, Pair<Integer, List<Integer>>> dict;

  public MyCyclicBarrier() {
    this.dict = new MyDict<>();
    this.nextFreeLocation = 1;
  }

  @Override
  public void put(Integer key, Pair<Integer, List<Integer>> value) {
    synchronized (dict) {
      dict.put(key, value);
      nextFreeLocation++;
    }
  }

  @Override
  public boolean isDefined(int key) {
    synchronized (dict) {
      return dict.isDefined(key);
    }
  }

  @Override
  public void update(int key, Pair<Integer, List<Integer>> value) throws CyclicBarrierException {
    synchronized (dict) {
      try {
        dict.update(key, value);
      } catch (DictionaryException e) {
        throw new CyclicBarrierException(e.getMessage());
      }
    }
  }

  @Override
  public List<Pair<Integer, List<Integer>>> getValues() {
    synchronized (dict) {
      return dict.getValues();
    }
  }

  @Override
  public IDict<Integer, Pair<Integer, List<Integer>>> deepCopy() {
    synchronized (dict) {
      return dict.deepCopy();
    }
  }

  @Override
  public Pair<Integer, List<Integer>> get(int key) throws CyclicBarrierException {
    synchronized (dict) {
      try {
        return dict.get(key);
      } catch (DictionaryException e) {
        throw new CyclicBarrierException(e.getMessage());
      }
    }
  }

  @Override
  public int getNextFreeLocation() {
    synchronized (dict) {
      return nextFreeLocation;
    }
  }

}
