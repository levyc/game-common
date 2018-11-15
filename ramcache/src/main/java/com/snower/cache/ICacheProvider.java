package com.snower.cache;
/**
 * 缓存提供者接口
 * @author levy
 *
 */
public interface ICacheProvider<K> {
	
	CacheEntry get(K key);
	
	void put(K k,CacheEntry entry);
	
	CacheEntry remove(K k);
	
	void clear();
	
}
