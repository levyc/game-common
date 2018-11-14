package com.snower.cache;

import java.util.concurrent.ConcurrentHashMap;

public class CacheProviderImpl<K, V> implements ICacheProvider<K, V> {

	private ConcurrentHashMap<K, V> cache;
	
	public V get(K key) {
		return cache.get(key);
	}

	public void put(K k, V value) {
		cache.put(k, value);
	}

	public V remove(K key) {
		return cache.remove(key);
	}

	public void clear() {
		cache.clear();
	}

}
