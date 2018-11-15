package com.snower.cache;

public class CacheEntry {
		
	private Object key;
	private Object value;
	
	public CacheEntry(Object key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}

	public Object getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}


	public void setValue(Object value) {
		this.value = value;
	}
	
	
}
