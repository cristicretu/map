package utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.util.Pair;

public class MySemaphore implements ISemaphore<Integer, Pair<Integer, List<Integer>>> {
  private Map<Integer, Pair<Integer, List<Integer>>> semaphoreTable;
  private AtomicInteger freeLocation;

  public MySemaphore() {
    this.semaphoreTable = new ConcurrentHashMap<>();
    this.freeLocation = new AtomicInteger(0);
  }

  @Override
  public synchronized void put(Integer key, Pair<Integer, List<Integer>> value) {
    semaphoreTable.put(key, value);
  }

  @Override
  public synchronized Pair<Integer, List<Integer>> get(Integer key) {
    return semaphoreTable.get(key);
  }

  @Override
  public synchronized boolean containsKey(Integer key) {
    return semaphoreTable.containsKey(key);
  }

  @Override
  public synchronized Map<Integer, Pair<Integer, List<Integer>>> getContent() {
    return new HashMap<>(semaphoreTable);
  }

  @Override
  public synchronized void update(Integer key, Pair<Integer, List<Integer>> value) {
    semaphoreTable.put(key, value);
  }

  @Override
  public synchronized Set<Integer> keySet() {
    return semaphoreTable.keySet();
  }

  @Override
  public synchronized Collection<Pair<Integer, List<Integer>>> values() {
    return semaphoreTable.values();
  }

  @Override
  public Integer getFreeLocation() {
    return freeLocation.getAndIncrement();
  }
}