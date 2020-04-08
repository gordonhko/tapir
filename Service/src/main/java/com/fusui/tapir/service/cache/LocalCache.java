package com.fusui.tapir.service.cache;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * @author gko
 */
//http://www.baeldung.com/guava-cache
public class LocalCache <K, V> implements ICache {
	
	private Cache <K,V> cache; 
	
	public LocalCache() {
		this (null);
	}
	public LocalCache(String name) {
		this (name, 24);
	}
	
	public LocalCache(String name, int lifespacInMinutes) {
		cache = CacheBuilder.newBuilder()
				.expireAfterWrite(lifespacInMinutes, TimeUnit.MINUTES)
			    //.maximumSize(2048)
			    .build(); 
	}

	public void put(K key, V object) {
		cache.put (key, object);
	}

	public V get(K key) {
		return cache.getIfPresent(key);
	}

	public V remove(K key) {
		// remove an object into the cache
		V result = cache.getIfPresent(key);
		cache.invalidate(key);
		return result;
	}
	
	public Set<Entry<K, V>> entrySet() {
		return cache.asMap().entrySet();
	}
	
	public void clear () {
		// based on evction policy
		cache.cleanUp();
	}
	

}










//public class ObjectCache <K, V> {
//
//	
//	
//	private static final String BACKEND_CONCURRENT_HASHMAP = "com.whirlycott.cache.impl.ConcurrentHashMapImpl";
//	private static final String POLICY_NULL = "com.whirlycott.cache.policy.NullPolicy";
//	
//	private Cache cache;
//	private long lifespan;
//	
//	public ObjectCache(String name) throws CacheException {
//		this (name, WflProperties.getWflProperties().getCacheDefaultObjectLifespanInHours());
//	}
//	
//	public ObjectCache(String name, int lifeSpanInHours) throws CacheException {
//		CacheConfiguration cc = new CacheConfiguration();
//		cc.setName(name);
//		cc.setBackend(BACKEND_CONCURRENT_HASHMAP);
//		cc.setTunerSleepTime(WflProperties.getWflProperties().getCacheCleanerIntervalInSeconds());
//		cc.setPolicy(POLICY_NULL);
//		//cc.setResetExpireFlag(false);
//		cache = CacheManager.getInstance().createCache(cc);
//		// convert to mil seconds
//		this.lifespan = lifeSpanInHours * 60 * 60 * 1000;
//		// GKO TODO
//		// make sure the following line removed
//		//this.lifespan = 5 * 1000;	// 5 seconds for testing purpose
//	}
//
//	public void put(K key, V object, long lifespan) {
//		// put an object into the cache
//		cache.store(key, object, lifespan);
//	}
//
//	public void put(K key, V bo) {
//		// put an object into the cache
//		//assert (bo.getOriginal() == null);
//		cache.store(key, bo, lifespan);
//	}
//
//	public V get(K key) {
//		// get the object back out of the cache
//		if (key == null) {
//			return null;
//		}
//		return (V) cache.retrieve(key);
//	}
//
//	public V remove(K key) {
//		// remove an object into the cache
//		return (V) cache.remove(key);
//	}
//	
//	public Set<Entry<K, V>> entrySet() {
//        return ( (Map) cache ).entrySet();
//	}
//	
//	public void clear () {
//		cache.clear();
//	}
//
//}
