package com.fusui.tapir.service.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dal.DataSourceFactory;

public class SqlDictionary {

	enum SqlKey {
		CREATE_TAPIR_SEQUENCE_SQL,
		DROP_TAPIR_SEQUENCE_SQL,
		SELECT_TAPIR_SEQUENCE_SQL,
		CREATE_MASTER_GROUPS_SQL, 
		DROP_MASTER_GROUPS_SQL, 
		CREATE_GROUPS_SQL, 
		DROP_GROUPS_SQL, 
		APPEND_MASTER_GROUPS_SQL,
		APPEND_GROUPS_SQL, 
		DELETE_GROUPS_SQL,
		SELECT_GROUPS_SQL
	}

	private static Logger logger = LoggerFactory.getLogger(SqlDictionary.class);

	private final static SqlDictionary instance = new SqlDictionary();

	private Properties prop = new Properties();
	private Properties dsProp = new Properties();

	public static SqlDictionary getInstance() {
		return instance;
	}

	private SqlDictionary() {
		
		try {
    		InputStream inputStream =  DataSourceFactory.class.getClassLoader().getResourceAsStream("datasource.properties");
			dsProp.load(inputStream);
  			inputStream.close();
    	}
    	catch (Throwable t) {
    		t.printStackTrace();
    		System.exit(0);
    	}
		
		try {
			InputStream inputStream = SqlDictionary.class.getClassLoader().getResourceAsStream("sql.properties");
			prop = new Properties();
			prop.load(inputStream);
			inputStream.close();
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(0);
		}
	}

	public Properties getDataSourceProperties() {
		return dsProp;
	}
	
	public String getSqlStmt(SqlKey key) {
		return prop.getProperty(key.name());
	}

	public void initSql() throws SQLException {

		List<SqlKey> keys = new ArrayList<SqlKey>();
		keys.add(SqlKey.DROP_TAPIR_SEQUENCE_SQL);
		keys.add(SqlKey.DROP_MASTER_GROUPS_SQL);
		keys.add(SqlKey.DROP_GROUPS_SQL);
		keys.add(SqlKey.CREATE_TAPIR_SEQUENCE_SQL);
		keys.add(SqlKey.CREATE_MASTER_GROUPS_SQL);
		keys.add(SqlKey.CREATE_GROUPS_SQL);
		
		
		Connection conn = DataSourceFactory.getInstance().getConnection();
		Statement stmt = conn.createStatement();
		for (SqlKey key : keys) {
			try {
				stmt.execute(getSqlStmt(key));
				conn.commit();
			}
			catch (Throwable t) {
				conn.rollback();
				System.out.println(t.getMessage());
			}
		}

	}

}
