package com.snower.cache;

/**
 * 缓存元素体
 */
public class CacheEntry<K, V> {

  /**
   * 键
   */
  private K key;

  /**
   * 值
   */
  private V value;

  public CacheEntry(K key, V value) {
    super();
    this.key = key;
    this.value = value;
  }

  public K getKey() {
    return key;
  }

  public V getValue() {
    return value;
  }

  public void setKey(K key) {
    this.key = key;
  }

  public void setValue(V value) {
    this.value = value;
  }


}
