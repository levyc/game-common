package com.snower.cache;

/**
 * 缓存提供者接口
 *
 * @author levy
 */
public interface ICacheProvider<K,V> {

  /**
   * 获取缓存
   */
  V get(K key);

  /**
   * 放入缓存
   */
  V put(K k, V v);

  /**
   * 放入缓存
   */
  V putIfAbsent(K k, V v);

  /**
   * 删除缓存
   */
  void remove(K k);

  /**
   * 删除缓存
   */
  void remove(K k,V v);


  /**
   * 清空缓存
   */
  void clear();

}
