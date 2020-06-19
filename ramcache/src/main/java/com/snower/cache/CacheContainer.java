package com.snower.cache;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import com.snower.data.IDataProvider;

/**
 * 緩存容器
 */
public class CacheContainer<K, V> {

  private String name;
  private ICacheProvider<K, V> cacheProvider;
  private IDataProvider<K, V> dataProvider;
  private ConcurrentHashMap<K, ReentrantLock> key2Locks = new ConcurrentHashMap<K, ReentrantLock>();


  /**
   * 要被更新的元素的key集合
   */
  private Set<K> willUpdate = Sets.newConcurrentHashSet();

  /**
   * 要被删除的元素的key集合
   */
  private Set<K> willRemove = Sets.newConcurrentHashSet();

  public CacheContainer(String name, ICacheProvider<K, V> cacheProvider,
      IDataProvider<K, V> dataProvider) {
    super();
    this.name = name;
    this.cacheProvider = cacheProvider;
    this.dataProvider = dataProvider;
  }

  @SuppressWarnings("unchecked")
  public V load(K k) {
    if (k == null) {
      return null;
    }

    if (willRemove.contains(k)) {
      return null;
    }

    CacheEntry<K, V> cacheEntry = cacheProvider.get(k);
    if (cacheEntry == null) {
      return null;
    }

    ReentrantLock lock = getLock(k);
    lock.lock();
    lock = ensureLock(k, lock);
    try {
      cacheEntry = cacheProvider.get(k);
      if (cacheEntry != null) {
        return cacheEntry.getValue();
      }

      V v = dataProvider.load(k);
      if (v == null) {
        cacheProvider.put(k, null);//防止缓存穿透
        return null;
      }
      cacheProvider.put(k, v);
      return v;
    } finally {
      lock.unlock();
    }
  }

  /**
   * 异步入库
   *
   * @param key Key
   * @param value Value
   */
  public V asyncAdd(K key, V value) {
    return doAdd(key, value, false);
  }

  /**
   * 同步入库
   *
   * @param key Key
   * @param value Value
   */
  public V add(K key, V value) {
    return doAdd(key, value, true);
  }

  /**
   * 执行入库操作
   *
   * @param key Key
   * @param value Value
   * @param persistNow 是否马上持久化
   */
  private V doAdd(K key, V value, boolean persistNow) {
    if (key == null || value == null) {
      return null;
    }

    ReentrantLock lock = getLock(key);
    lock.lock();
    lock = ensureLock(key, lock);
    try {
      if (willRemove.contains(key)) {
        willRemove.remove(key);
      }

      CacheEntry<K, V> cacheEntry = cacheProvider.get(key);
      if (value != cacheEntry.getValue()) {
        cacheEntry.setNew(false);
        cacheEntry.setValue(value);
      } else {
        cacheProvider.put(key, value);
        cacheEntry = cacheProvider.get(key);
        cacheEntry.setNew(true);
      }
      if (persistNow) {
        dataProvider.insert(key, value);
      } else {
        willUpdate.add(key);
      }
      return cacheEntry.getValue();
    } finally {
      lock.unlock();
    }
  }


  /**
   * 只更新缓存,等待定时持久化
   */
  public void update(K key, V value) {
    if (key == null || value == null) {
      return;
    }
    ReentrantLock lock = getLock(key);
    lock.lock();
    try {
      CacheEntry entry = cacheProvider.get(key);
      if (entry == null || entry.getValue() == null) {
        dataProvider.save(key, value);
      }
//      cacheProvider.put(key, new CacheEntry(key, value));
    } finally {
      lock.unlock();
    }
  }

  /**
   * 更新缓存后立即持久化
   */
  public void updateNow(K key, V value) {
    if (key == null || value == null) {
      return;
    }
    ReentrantLock lock = getLock(key);
    lock.lock();
    try {
      CacheEntry entry = cacheProvider.get(key);
      if (entry == null || entry.getValue() == null) {
        dataProvider.save(key, value);
      }
//      cacheProvider.put(key, new CacheEntry(key, value));
    } finally {
      lock.unlock();
    }
  }

  /**
   * 根据Key获取锁
   *
   * @param k key
   */
  private ReentrantLock getLock(K k) {
    ReentrantLock reentrantLock = key2Locks.get(k);
    if (reentrantLock == null) {
      reentrantLock = new ReentrantLock();
      ReentrantLock before = key2Locks.putIfAbsent(k, reentrantLock);
      reentrantLock = before != null ? before : reentrantLock;
    }
    return reentrantLock;
  }

  /**
   * 确保参数的锁与map里的锁是同一把锁
   *
   * @param key key
   * @param lock 要判断是否与map里的锁一致的锁
   */
  private ReentrantLock ensureLock(K key, ReentrantLock lock) {
    for (ReentrantLock newLock = getLock(key); newLock != lock; newLock = getLock(key)) {
      lock.unlock();
      lock = newLock;
      newLock.lock();
    }
    return lock;
  }

  public String getName() {
    return name;
  }


}
