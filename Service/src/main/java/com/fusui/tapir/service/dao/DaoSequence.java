package com.fusui.tapir.service.dao;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dal.DataSourceFactory;

// Postgres manual
// http://www.postgresql.org/docs/8.1/static/sql-createsequence.html
public abstract class DaoSequence {
	
	private static Logger logger = LoggerFactory.getLogger(DaoSequence.class);

	public static final int INCREMENT_BY = 16;
	private static DaoSequence s_singleton = null;

	private long seqNumber = 0;
	private long seqBoundary = 0;
	
	protected abstract long getNextNumber() throws SQLException;
//	public abstract void createTable() throws SQLException;
//	public abstract void dropTable() throws SQLException;
	
	public static DaoSequence getInstance() {
		try {
			if (null == s_singleton) {
				synchronized (DaoSequence.class) {
					if (null == s_singleton) {
						
						InputStream inputStream =  DataSourceFactory.class.getClassLoader().getResourceAsStream("datasource.properties");
						Properties prop = new Properties();
			  			prop.load(inputStream);
			  			inputStream.close();
						
						String driver = SqlDictionary.getInstance().getDataSourceProperties().getProperty("com.fusui.tapir.db.driver");
						
						if (driver.equals("org.postgresql.Driver") ) {				
							s_singleton  = new DaoPgSequence();
						}
						else if (driver.equals("com.mysql.jdbc.Driver") ) {
							s_singleton  = new DaoMySqlSequence();
						}
						else {
							logger.error("no sequence is available for driver="+driver);
							System.exit(0);
						}
					}
				}
			}
		}
		catch (Throwable t) {
			t.printStackTrace();
			s_singleton = null;
		}
		return s_singleton;
	}
	
	public synchronized Long getUniqueId() throws SQLException {
		if (seqNumber >= seqBoundary ) {
			seqNumber = getNextNumber();
			seqBoundary = seqNumber + INCREMENT_BY;
		}

		
		return seqNumber++;
		
	}
		
}
