package utils;

import java.util.List;

import exceptions.LockException;

public interface ILock<K, V> {
  void put(K key, V value);

  V get(K key) throws LockException;

  boolean isDefined(K key);

  void update(K key, V value) throws LockException;

  List<V> getValues();

  ILock<K, V> deepCopy();

  int getNewFreeLock();

}
