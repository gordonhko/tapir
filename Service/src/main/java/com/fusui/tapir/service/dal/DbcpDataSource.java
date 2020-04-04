package com.fusui.tapir.service.dal;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DbcpDataSource implements DataSource {

	private static final Logger logger = LoggerFactory.getLogger(DbcpDataSource.class);
    private BasicDataSource ds;

    @Override
    public void init(Properties prop) {
    	
  		String driver = (String) prop.get("com.fusui.tapir.db.driver");
  		String url = (String) prop.get("com.fusui.tapir.db.url");
  		String username = (String) prop.get("com.fusui.tapir.db.username");
  		String password = (String) prop.get("com.fusui.tapir.db.password");
  			
  		ds = new BasicDataSource();
	    ds.setDriverClassName(driver);
	    ds.setUrl(url);
	    ds.setUsername(username);
	    ds.setPassword(password);
	    ds.setDefaultAutoCommit(false);    
	    
	    
//        ds.setDriverClassName("org.postgresql.Driver");
//        ds.setUrl("jdbc:postgresql://localhost:5432/activiti");
//        ds.setUsername("postgres");
//        ds.setPassword("postgres");
        
        // the settings below are optional -- dbcp can work with defaults
//        ds.setMinIdle(5);
//        ds.setMaxIdle(20);
//        ds.setMaxOpenPreparedStatements(180);
	}

    @Override
    public Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

}
