package com.snower;

import com.snower.cache.ICacheProvider;
import java.util.concurrent.ConcurrentHashMap;

public class CacheManager {

  private ConcurrentHashMap<String, ICacheProvider> caches = new ConcurrentHashMap<String, ICacheProvider>();

}
