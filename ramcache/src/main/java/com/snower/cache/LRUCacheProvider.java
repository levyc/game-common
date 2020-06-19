package com.snower.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于LRU的缓存实现
 *
 * @author levy
 */
public class LRUCacheProvider<K, CacheEntry> implements ICacheProvider<K, CacheEntry> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private ConcurrentMap<K, CacheEntry> cache;

  /**
   * 最大容量
   */
  private int maxSize;

  /**
   * 初始化容量
   */
  private int initSize;

  /**
   * time to live (Second)
   */
  private int ttl;

  private EvictionListener<K, CacheEntry> evictionListener;


  public LRUCacheProvider(int maxSize, int initSize, int ttl) {
    this.maxSize = maxSize;
    this.initSize = initSize;
    this.ttl = ttl;

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

    Cache<K, CacheEntry> cache = CacheBuilder.newBuilder().maximumSize(maxSize)
        .initialCapacity(initSize)
        .expireAfterAccess(ttl,
            TimeUnit.SECONDS).removalListener(removalListener).build();
    this.cache = cache.asMap();
  }

  public CacheEntry get(K key) {
    return cache.get(key);
  }

  public CacheEntry put(K k, CacheEntry entry) {
    return cache.put(k, entry);
  }

  public CacheEntry putIfAbsent(K k, CacheEntry cacheEntry) {
    return cache.putIfAbsent(k, cacheEntry);
  }


  public void remove(K key) {
    cache.remove(key);
  }

  public void remove(K key, CacheEntry entry) {
    cache.remove(key, entry);
  }


  public void clear() {
    cache.clear();
  }

}
