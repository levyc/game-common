package com.snower.data;

/**
 * @author levy
 */
public interface IDataProvider<K, V> {

  V load(K key);

  void insert(K key, V value);

  void update(K key, V value);

  void delete(K key);
}
