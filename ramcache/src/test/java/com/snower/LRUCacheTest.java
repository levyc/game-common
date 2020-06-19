package com.snower;


import com.snower.cache.CacheEntry;
import com.snower.cache.ICacheProvider;
import com.snower.cache.LRUCacheProvider;
import org.junit.Test;

public class LRUCacheTest {

  @Test
  public void testLRU() {
    ICacheProvider<String, CacheEntry> cache = new LRUCacheProvider<String, CacheEntry>(2, 1, 5);
    cache.put("1", new CacheEntry("1", "1"));
    cache.put("2", new CacheEntry("2", "2"));
    cache.remove("1");
    cache.put("3", new CacheEntry("3", "3"));
  }

}
