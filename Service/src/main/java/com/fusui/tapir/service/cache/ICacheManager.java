package com.fusui.tapir.service.cache;

import java.util.Properties;

/**
 * @author gko
 */
//http://www.baeldung.com/guava-cache
public interface ICacheManager <K,V> {

	void init(Properties prop);

	ICache getCache(Long tenantId, String name);
}