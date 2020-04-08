package com.fusui.tapir.service.cache;

import java.io.IOException;
import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

//import oracle.jdbc.pool.OracleDataSource;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Ignore
public class JedisClientTest {
	
	private final static Long USER_ID = 37692L;
	private final static Long WORKSPACE_ID = 61070L;
	private final static Long ITEM_DESCRIPTION_ATTRIBUTE_ID = 14L;
	
	@BeforeClass
	public static void openTest() throws IOException, SQLException {
		String driver = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@sand12db-sid.dev.bom.com:1521:sand12db";
		String username = "app";
		String password = "d0ntsharepswd!";
//	    OracleDataSource datasource = new OracleDataSource();
//        datasource.setURL(url);
//        datasource.setUser(username);
//        datasource.setPassword(password);
		
		// for sand12, 
		// hostname = redis.srv.sand.bom.com, copied from webid
		// port = 6379+12
		// timeout = 2000 (ms), copied from redis.clients.jedis.DEFAULT_TIMEOUT
		JedisPoolConfig poolConfig = new JedisPoolConfig();
		JedisPool pool = new JedisPool(poolConfig, "redis.srv.sand.bom.com", 6391, 2000, "forgetmenot");
		//new DataAccessManagerBuilder(datasource, pool).id("Amur").build();
		//DataAccessManager.init(datasource, pool);
	}
	
	@AfterClass
    public static void closeTest() {
        /**
         * This method will be called only once per test class. It will be called 
         * after executing test.  
         */
    }

	
	@Test
	public final void testCache() throws IOException, SQLException {
		
	}

	
	
}
