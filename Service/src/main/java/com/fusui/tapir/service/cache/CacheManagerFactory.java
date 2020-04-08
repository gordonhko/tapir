package com.fusui.tapir.service.cache;

import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dao.ResourceDictionary;

public class CacheManagerFactory {

	private static final Logger logger = LoggerFactory.getLogger(CacheManagerFactory.class);
    private static final CacheManagerFactory  instance = new CacheManagerFactory();
    private ICacheManager manager;

    private CacheManagerFactory() {
    	try {
    		Properties prop = ResourceDictionary.getInstance().getDataSourceProperties();
    		String classname = (String) prop.get("com.fusui.tapir.cache");
    		manager = (ICacheManager) Class.forName(classname).newInstance();
    		manager.init(prop);
    	}
    	catch (Throwable t) {
    		t.printStackTrace();
    		System.exit(0);
    	}
    }

    public static CacheManagerFactory getInstance()  {
    	return instance;
    }

    public ICache  getCache(Long tenantId, String name) throws SQLException  {
        return manager.getCache(tenantId, name);
    }

}
