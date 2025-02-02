package utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MySem implements ISem<Integer, Tuple<Integer, List<Integer>, Integer>> {
  private Map<Integer, Tuple<Integer, List<Integer>, Integer>> semaphoreTable;
  private AtomicInteger freeLocation;

  public MySem() {
    semaphoreTable = new ConcurrentHashMap<>();
    freeLocation = new AtomicInteger(0);
  }

  @Override
  public synchronized void put(Integer key, Tuple<Integer, List<Integer>, Integer> value) {
    semaphoreTable.put(key, value);
  }

  @Override
  public synchronized Tuple<Integer, List<Integer>, Integer> get(Integer key) {
    return semaphoreTable.get(key);
  }

  @Override
  public synchronized boolean containsKey(Integer key) {
    return semaphoreTable.containsKey(key);
  }

  @Override
  public synchronized Map<Integer, Tuple<Integer, List<Integer>, Integer>> getContent() {
    return new HashMap<>(semaphoreTable);
  }

  @Override
  public synchronized void update(Integer key, Tuple<Integer, List<Integer>, Integer> value) {
    semaphoreTable.put(key, value);
  }

  @Override
  public Set<Integer> keySet() {
    return semaphoreTable.keySet();
  }

  @Override
  public Collection<Tuple<Integer, List<Integer>, Integer>> values() {
    return semaphoreTable.values();
  }

  @Override
  public Integer getFreeLocation() {
    return freeLocation.getAndIncrement();
  }

  @Override
  public String toString() {
    return semaphoreTable.toString();
  }

}
