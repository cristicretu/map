package utils;

import java.util.List;
import java.util.Map;

import exceptions.LatchException;

public interface ILatchTable<K, V> {
  int get(int position) throws LatchException;

  void put(K key, V value) throws LatchException;

  void remove(K key);

  boolean isDefined(K key);

  String toString();

  Map<K, V> getLatchTable();

  List<V> getValues();

  void setLatchTable(Map<K, V> dictionary);

  int getFirstFreeLocation();
}