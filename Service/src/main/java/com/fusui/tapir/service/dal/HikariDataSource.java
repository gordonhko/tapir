package com.fusui.tapir.service.dal;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;

public class HikariDataSource implements IDataSource {

	private static final Logger logger = LoggerFactory.getLogger(HikariDataSource.class);
    private  com.zaxxer.hikari.HikariDataSource ds;
 
    @Override
    public void init(Properties prop) {
    	
    	 HikariConfig config = new HikariConfig(prop);
    	
//    	 config.setJdbcUrl( "jdbc_url" );
//       config.setUsername( "database_username" );
//       config.setPassword( "database_password" );
//       config.addDataSourceProperty( "cachePrepStmts" , "true" );
//       config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
//       config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
    	
    	 ds = new com.zaxxer.hikari.HikariDataSource( config );
    	
  	}

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
