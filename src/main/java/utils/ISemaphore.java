package utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import javafx.util.Pair;

public interface ISemaphore<K, V> {
  void put(K key, Pair<Integer, List<Integer>> value);

  Pair<Integer, List<Integer>> get(K key);

  boolean containsKey(K key);

  Map<K, Pair<Integer, List<Integer>>> getContent();

  void update(K key, Pair<Integer, List<Integer>> value);

  Set<K> keySet();

  Collection<Pair<Integer, List<Integer>>> values();

  K getFreeLocation();
}