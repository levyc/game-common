package com.snower;

import com.google.common.collect.Sets;
import com.snower.cache.CacheEntry;
import com.snower.cache.ICacheProvider;
import com.snower.cache.LRUCacheProvider;
import com.snower.data.IDataProvider;
import com.snower.utils.thread.NamedThreadFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 緩存容器
 */
public class CacheContainer<K, V> {

  private Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 缓存容器名字
   */
  private String name;
  /**
   * 内存缓存接口
   */
  private ICacheProvider<K, V> cacheProvider;
  /**
   * 数据来源(数据库)操作接口
   */
  private IDataProvider<K, V> dataProvider;
  /**
   * KEY对应的锁
   */
  private ConcurrentHashMap<K, ReentrantLock> key2Locks = new ConcurrentHashMap<K, ReentrantLock>();
  /**
   * 要被更新的元素的key集合
   */
  private Set<K> willUpdate = Sets.newConcurrentHashSet();

  /**
   * 要被删除的元素的key集合
   */
  private Set<K> willRemove = Sets.newConcurrentHashSet();

  /**
   * 缓存最大数量
   */
  private static int maxCacheSize = 3000;

  /**
   * 缓存初始化数量
   */
  private static int initCacheSize = 100;

  /**
   * 持久化操作时间间隔(毫秒)
   */
  private long persistInterval;

  private ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors
      .newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
          new NamedThreadFactory(name + "缓存持久化处理线程组", "缓存持久化处理"));

  public CacheContainer(String name, IDataProvider dataProvider) {
    this(name, new LRUCacheProvider(maxCacheSize, initCacheSize), dataProvider, 10000);
  }

  public CacheContainer(final String name, final ICacheProvider<K, V> cacheProvider,
      final IDataProvider<K, V> dataProvider) {
    this(name, new LRUCacheProvider(maxCacheSize, initCacheSize), dataProvider, 10000);

  }

  public CacheContainer(final String name, final ICacheProvider<K, V> cacheProvider,
      final IDataProvider<K, V> dataProvider, final long persistInterval) {
    super();

    if (persistInterval < 0) {
      throw new RuntimeException(name + "缓存容器persistInterval为负数！！！！");
    }

    this.name = name;
    this.cacheProvider = cacheProvider;
    this.dataProvider = dataProvider;
    this.persistInterval = persistInterval;
    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          if (executorService.getQueue().isEmpty()) {
            submitPersistTask();
            continue;
          }
          try {
            Thread.sleep(CacheContainer.this.persistInterval);
          } catch (InterruptedException e) {
            logger.error("[{}]定时持久化线程中断异常", name);
          }
        }
      }
    }).start();
  }

  /**
   * 将更新队列和删除队列提交到线程池中处理
   */
  private void submitPersistTask() {
    if (!willRemove.isEmpty()) {
      for (final K k : willRemove) {
        willRemove.remove(k);
        willUpdate.remove(k);

        cacheProvider.remove(k);
        key2Locks.remove(k);

        //提交到线程池执行
        executorService.submit(new Runnable() {
          @Override
          public void run() {
            dataProvider.delete(k);
          }
        });
      }
    }

    if (!willUpdate.isEmpty()) {
      for (final K k : willUpdate) {
        willUpdate.remove(k);

        final CacheEntry<K, V> cacheEntry = cacheProvider.get(k);
        if (cacheEntry == null || cacheEntry.getValue() == null) {
          logger.error("!!!!!!![{}]模块异步更新一个不存在的数据,检查业务逻辑是否正确,id:[{}]", name, k.toString());
          continue;
        }

        if (cacheEntry.isNew()) {
          executorService.submit(new Runnable() {
            @Override
            public void run() {
              dataProvider.insert(k, cacheEntry.getValue());
            }
          });
        } else {
          executorService.submit(new Runnable() {
            @Override
            public void run() {
              dataProvider.update(k, cacheEntry.getValue());
            }
          });
        }
      }
    }
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
    if (cacheEntry != null) {
      return cacheEntry.getValue();
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

      CacheEntry<K, V> cacheEntry = cacheProvider.putIfAbsent(key, value);
      if (cacheEntry != null) {
        if (cacheEntry.getValue() != null) {
          logger.error("[{}]模块重复插入实体,id:[{}]", name, key.toString());
          cacheEntry.setValue(value);
          cacheEntry.setNew(false);
        } else {
          cacheEntry.setValue(value);
          cacheEntry.setNew(true);
        }
      } else {
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
   * 即时更新入库
   */
  public void update(K key, V value) {
    doUpdate(key, value, true);
  }

  /**
   * 执行更新操作
   *
   * @param persistNow 是否立即入库
   */
  private void doUpdate(K key, V value, boolean persistNow) {
    if (key == null || value == null) {
      return;
    }
    ReentrantLock lock = getLock(key);
    lock.lock();
    try {
      CacheEntry entry = cacheProvider.get(key);
      if (entry == null || entry.getValue() == null) {
        logger.error("!!!!!!![{}]模块更新一个不存在的数据,检查业务逻辑是否正确,id:[{}]", name, key.toString());
        cacheProvider.put(key, value);
      } else {
        if (entry.getValue() != null && entry.getValue() != value) {
          logger.error("!!!!!!![{}]模块更新的实体与原实体不一致,检查业务逻辑是否正确,id:[{}]", name, key.toString());
        }
        entry.setValue(value);
      }

      if (persistNow) {
        dataProvider.update(key, value);
      } else {
        willUpdate.add(key);
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * 异步更新入库
   */
  public void asyncUpdate(K key, V value) {
    doUpdate(key, value, false);
  }

  public void delete(K key) {
    ReentrantLock lock = getLock(key);
    lock.lock();
    lock = ensureLock(key, lock);
    try {
      willUpdate.remove(key);
      willRemove.remove(key);
      cacheProvider.remove(key);
      dataProvider.delete(key);
      key2Locks.remove(key);
    } finally {
      lock.unlock();
    }
  }

  public void asycDelete(K key) {
    ReentrantLock lock = getLock(key);
    lock.lock();
    lock = ensureLock(key, lock);
    try {
      willRemove.add(key);
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
