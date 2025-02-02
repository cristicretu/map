package utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ISem<K, V> {
  void put(K key, Tuple<Integer, List<Integer>, Integer> value);

  Tuple<Integer, List<Integer>, Integer> get(K key);

  boolean containsKey(K key);

  Map<K, Tuple<Integer, List<Integer>, Integer>> getContent();

  void update(K key, Tuple<Integer, List<Integer>, Integer> value);

  Set<K> keySet();

  Collection<Tuple<Integer, List<Integer>, Integer>> values();

  K getFreeLocation();

}
