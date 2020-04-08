package com.fusui.tapir.service.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dal.DataSourceFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * @author gko
 */
//http://www.baeldung.com/guava-cache
public class RedisCache <K, V> implements ICache {
	
	private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

	private JedisPool jedisPool;
	// jackson object mapper is thread-safe
	//private static ObjectMapper mapper = new ObjectMapper();
	
	
	RedisCache(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}
	
	//de-constructor
	public void deinit() throws Exception {
		this.jedisPool.destroy();
	}
	
	private Jedis openJedisSession() {
		return jedisPool.getResource();
	}

	private void closeJedisSession(Jedis jedis) {
		if (jedis != null) {
			jedis.close();
		}
	}
	
	private String getString(String redisKey) {
		Jedis jedis = null;
		try {
			jedis = openJedisSession();
			return jedis.get(redisKey);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			return null;
		} finally {
			closeJedisSession(jedis);
		}
	}

	private String setString(String redisKey, String value) {
		if (value == null ) {
			return null;
		}
		
		Jedis jedis = null;
		try {
			jedis = openJedisSession();
			return jedis.set(redisKey, value);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			return null;
		} finally {
			closeJedisSession(jedis);
		}
	}
}
	

//	

//
//	public Boolean delete(String key1, String key2) {
//	    return true;
//	    
////		Jedis jedis = null;
////		try {
////			jedis = openJedisSession();
////			if (key2 == null) {
////				jedis.del(key1);
////			}
////			else {
////				jedis.del(key1, key2);
////			}
////			return true;
////		} catch (Throwable t) {
////			logger.error(t.getMessage(), t);
////			return false;
////		} finally {
////			closeJedisSession(jedis);
////		}
//	}
//	
//
//
////	public String toString() {
////		// String dataSourceType = WflProperties.getWflProperties().getDsType();
////		StringBuilder sb = new StringBuilder();
////
////		sb.append("--- Redis ---");
////		sb.append("jedis =" + jedisPool);
////		sb.append("\n");
////
////		return sb.toString();
////	}
//
//}
