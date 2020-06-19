package com.snower;


import com.snower.cache.CacheEntry;
import com.snower.cache.ICacheProvider;
import com.snower.cache.LRUCacheProvider;
import org.junit.Assert;
import org.junit.Test;

public class LRUCacheTest {

  @Test
  public void testLRU() {
    ICacheProvider<String, String> cache = new LRUCacheProvider<String, String>(2, 1, 5);
    cache.put("1", "1");
    cache.put("2", "2");
    cache.put("3", "3");
    Assert.assertTrue(cache.size() == 2);

    try {
      Thread.sleep(7000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    System.out.println("size:" + cache.size());
    CacheEntry<String, String> cacheEntry = cache.get("2");
    System.out.println("1:" + cacheEntry.getValue());
    cache.get("3");
    System.out.println("size:" + cache.size());

    Assert.assertTrue(cache.size() == 0);
  }

}
