package com.fusui.tapir.service.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalCacheManager implements ICacheManager {

	private static final Logger logger = LoggerFactory.getLogger(LocalCacheManager.class);

	// RowBase -> Map (tenantID, rowType, List<value> )
	// Resource -> Map (tenantID, rscType+rscId, value)
	// Metadata -> Map (tenantID, rscType, List<value> )
	// PossibleValue -> Map (tenantID, rscType+metaID, List<value> )
	// UserRule -> Map (tenantID, userID, List<value>)

	private Map<Long, Map<String, ICache>> lookup = new HashMap<Long, Map<String, ICache>>();

	@Override
	public void init(Properties prop) {
	}

	@Override
	public ICache getCache(Long tenantId, String name) {

		Map<String, ICache> map = lookup.get(tenantId);
		if (map == null) {
			synchronized (this) {
				map = lookup.get(tenantId);
				if (map == null) {
					map = new HashMap<String, ICache>();
					lookup.put(tenantId, map);
				}
			}
		}

		ICache cache = map.get(name);
		if (cache == null) {
			synchronized (this) {
				cache = map.get(name);
				if (map == null) {
					cache = new LocalCache();
					map.put(name, cache);
				}
			}
		}
		return cache;
	}

}
