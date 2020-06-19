package com.snower.cache;

import com.google.common.base.Objects;

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

  /**
   * 新元素
   */
  private boolean isNew;

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

  public boolean isNew() {
    return isNew;
  }

  public void setNew(boolean aNew) {
    isNew = aNew;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CacheEntry<?, ?> that = (CacheEntry<?, ?>) o;
    return Objects.equal(key, that.key) &&
        Objects.equal(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key, value);
  }
}
