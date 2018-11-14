package com.snower.cache;
/**
 * 缓存提供者接口
 * @author levy
 *
 */
public interface ICacheProvider<K,V> {
	
	V get(K key);
	
	void put(K k,V v);
	
	V remove(K k);
	
	void clear();
	
}
