package com.snower.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheProviderImpl<K> implements ICacheProvider<K> {

	private ConcurrentHashMap<K,CacheEntry> cache;
	
	public CacheEntry get(K key) {
		return cache.get(key);
	}

	public void put(K k, CacheEntry value) {
		cache.put(k, value);
	}

	public CacheEntry remove(K key) {
		return cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}

}
