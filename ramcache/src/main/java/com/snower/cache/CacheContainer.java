package com.snower.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import com.snower.data.IDataProvider;

public class CacheContainer<K,V> {
	
	private String name;
	private ICacheProvider<K> cacheProvider;
	private IDataProvider<K, V> dataProvider;
	private ConcurrentHashMap<K,ReentrantLock> key2Locks = new ConcurrentHashMap<K,ReentrantLock>();
	private AtomicInteger loadCounter = new AtomicInteger(0);
	private AtomicInteger hitCounter = new AtomicInteger(0);
	
	public CacheContainer(String name,ICacheProvider<K> cacheProvider, IDataProvider<K, V> dataProvider) {
		super();
		this.name = name;
		this.cacheProvider = cacheProvider;
		this.dataProvider = dataProvider;
	}
	
	@SuppressWarnings("unchecked")
	public V load(K k){
		if(k==null){
			return null;
		}
		
		loadCounter.incrementAndGet();
		ReentrantLock lock = getLock(k);
		lock.lock();
		try {
			CacheEntry cacheEntry = cacheProvider.get(k);
			if(cacheEntry!=null){
				hitCounter.incrementAndGet();
				return (V) cacheEntry.getValue();
			}
			
			V v = dataProvider.load(k);
			if(v==null){
				return null;
			}
			
			CacheEntry entry = new CacheEntry(k, v);
			cacheProvider.put(k, entry);
			return v;
		} finally {
			lock.unlock();
		}
	}
	
	@SuppressWarnings("unchecked")
	public V add(K key,V value){
		if(key==null||value==null){
			return null;
		}
		
		ReentrantLock lock = getLock(key);
		lock.lock();
		try {
			CacheEntry oldEntry = cacheProvider.get(key);
			cacheProvider.put(key, new CacheEntry(key, value));
			dataProvider.insert(key, value);
			return (V) oldEntry.getValue();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 只更新缓存,等待定时持久化
	 * @param key
	 * @param value
	 */
	public void update(K key,V value){
		if(key==null||value==null){
			return ;
		}
		ReentrantLock lock = getLock(key);
		lock.lock();
		try {
			CacheEntry entry = cacheProvider.get(key);
			if(entry==null||entry.getValue()==null){
				dataProvider.save(key, value);
			}
			cacheProvider.put(key, new CacheEntry(key, value));
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * 更新缓存后立即持久化
	 * @param key
	 * @param value
	 */
	public void updateNow(K key,V value){
		if(key==null||value==null){
			return ;
		}
		ReentrantLock lock = getLock(key);
		lock.lock();
		try {
			CacheEntry entry = cacheProvider.get(key);
			if(entry==null||entry.getValue()==null){
				dataProvider.save(key, value);
			}
			cacheProvider.put(key, new CacheEntry(key, value));
		} finally {
			lock.unlock();
		}
	}
	
	private ReentrantLock getLock(K k){
		ReentrantLock reentrantLock = key2Locks.get(k);
		if(reentrantLock==null){
			reentrantLock = new ReentrantLock();
			ReentrantLock before = key2Locks.putIfAbsent(k,reentrantLock);
			reentrantLock = before!=null?before:reentrantLock;
		}
		return reentrantLock;
	}

	public String getName() {
		return name;
	}
	
	
	
	
	
}
