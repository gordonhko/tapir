package com.fusui.tapir.service.dal;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dao.SqlDictionary;

public class DataSourceFactory {

	private static final Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);
    private static final DataSourceFactory  instance = new DataSourceFactory();
    private DataSource ds;

    private DataSourceFactory() {
    	try {
    		Properties prop = SqlDictionary.getInstance().getDataSourceProperties();
    		String classname = (String) prop.get("com.fusui.tapir.db.connection.pool");
  			ds = (DataSource) Class.forName(classname).newInstance();
  			ds.init(prop);
    	}
    	catch (Throwable t) {
    		t.printStackTrace();
    		System.exit(0);
    	}
    }

    public static DataSourceFactory getInstance()  {
    	return instance;
    }

    public Connection getConnection() throws SQLException  {
        return ds.getConnection();
    }

}
