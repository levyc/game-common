package com.snower.cache;

/**
 * 失效监听器
 *
 * @author levy
 */
public interface EvictionListener<K, V> {


  /**
   * 元素失效时处理
   */
  void onEviction(K k, V v);


}
