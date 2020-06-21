package com.snower.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于LRU的缓存实现
 *
 * @author levy
 */
public class LRUCacheProvider<K, V> implements ICacheProvider<K, V> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private ConcurrentMap<K, CacheEntry<K, V>> cache;

  /**
   * 最大容量
   */
  private int maxSize;

  /**
   * 初始化容量
   */
  private int initSize;


  private EvictionListener<K, CacheEntry<K, V>> evictionListener;


  public LRUCacheProvider(int maxSize, int initSize) {
    this.maxSize = maxSize;
    this.initSize = initSize;

    RemovalListener removalListener = new RemovalListener<K, CacheEntry>() {
      public void onRemoval(RemovalNotification<K, CacheEntry> notification) {
        logger.debug("元素失效,类型:[{}],ID:[{}],原因:[{}]",
            notification.getValue().getClass().getSimpleName(), notification.getKey(),
            notification.getCause());
        System.out.println("元素失效,类型:[{}],ID:[{}],原因:[{}]" +
            notification.getValue().getClass().getSimpleName().toString() + ":" + notification
            .getKey()
            .toString() + ":" +
            notification.getCause().toString());
        if (evictionListener != null) {
          evictionListener.onEviction(notification.getKey(), notification.getValue());
        }
      }
    };

    Cache<K, CacheEntry<K, V>> cache = CacheBuilder.newBuilder().maximumSize(maxSize)
        .initialCapacity(initSize).removalListener(removalListener).build();
    this.cache = cache.asMap();
  }


  @Override
  public CacheEntry<K, V> get(K key) {
    return cache.get(key);
  }

  @Override
  public CacheEntry<K, V> put(K k, V v) {
    return cache.put(k, new CacheEntry<K, V>(k, v));
  }

  @Override
  public CacheEntry<K, V> putIfAbsent(K key, V v) {
    return cache.putIfAbsent(key, new CacheEntry<K, V>(key, v));
  }

  public void remove(K key) {
    cache.remove(key);
  }

  @Override
  public void remove(K key, CacheEntry<K, V> entry) {
    cache.remove(key, entry);
  }


  public void clear() {
    cache.clear();
  }

  @Override
  public int size() {
    return cache.size();
  }

}
