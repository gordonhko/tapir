package com.fusui.tapir.service.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;

//http://stackoverflow.com/questions/9046971/mysql-equivalent-of-orFoundryUtiles-sequence-nexvalpublic class 
//http://dev.mysql.com/doc/refman/5.0/en/innodb-locking-reads.html
public class DaoMySqlSequence extends DaoSequence {

	private static Logger logger = LoggerFactory.getLogger(DaoMySqlSequence.class);
	

//	MySQL's last_insert_id() is dependable in that it's always the LAST insert performed by THAT PARTICULAR connection. 
//	It won't report an insert id created by some other connection, it won't report an insert that you did two connections ago. 
//	It won't matter which cpu core the actual insert occured on, and which core the last_insert_id() call is processed on. 
//	It will always be the right ID number for that connection.
//
//	If you roll back a transaction that did an insert, last_insert_id() will STILL report that new id, 
//	even though it no longer exists. The id will not be reused, however, in a subsequent insert

	private static final String UPDATE_SEQUENCE_SQL = "UPDATE objectid SET id=LAST_INSERT_ID(id+"+ DaoSequence.INCREMENT_BY +");";
	private static final String SELECT_SEQUENCE_SQL = "SELECT LAST_INSERT_ID();";

	

	protected DaoMySqlSequence() {
		// use singleton instead of generating a new object
	}



	@Override
	protected long getNextNumber() throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			
			// will have trouble if executed by more than 1 process
			stmt = conn.createStatement();
			int nRow = stmt.executeUpdate(UPDATE_SEQUENCE_SQL);
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SELECT_SEQUENCE_SQL);
			
			
			rs.next();
			int count = rs.getInt(1);
			
			System.out.println("getNextNumber="+count);
			
			return count;
		} catch (SQLException e) {
			logger.info("DaoSequence error: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}

			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}

	}

}
