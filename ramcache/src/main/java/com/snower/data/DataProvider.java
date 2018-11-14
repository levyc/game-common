package com.snower.data;
/**
 * 
 * @author levy
 *
 * @param <K>
 * @param <V>
 */
public interface DataProvider<K,V> {
	
	V load(K key);
	
	void insert(K key,V value);
	
	void save(K key,V value);
	
	void delete(K key,V value);
}
