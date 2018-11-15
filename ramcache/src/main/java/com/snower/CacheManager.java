package com.snower;

import java.util.concurrent.ConcurrentHashMap;

import com.snower.cache.ICacheProvider;

public class CacheManager {
	
	private ConcurrentHashMap<String,ICacheProvider> caches = new ConcurrentHashMap<String,ICacheProvider>();
	
}
