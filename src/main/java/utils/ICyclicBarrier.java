package utils;

import java.util.List;

import exceptions.CyclicBarrierException;
import javafx.util.Pair;

public interface ICyclicBarrier {
  void put(Integer key, Pair<Integer, List<Integer>> value);

  Pair<Integer, List<Integer>> get(int key) throws CyclicBarrierException;

  boolean isDefined(int key);

  void update(int key, Pair<Integer, List<Integer>> value) throws CyclicBarrierException;

  List<Pair<Integer, List<Integer>>> getValues();

  IDict<Integer, Pair<Integer, List<Integer>>> deepCopy();

  int getNextFreeLocation();
}