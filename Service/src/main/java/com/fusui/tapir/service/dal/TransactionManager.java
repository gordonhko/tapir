package com.fusui.tapir.service.dal;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * this class acts as a container to our thread local variables.
 */

public class TransactionManager {

	public static class DaoTransContext {
		private Connection conn;
		private Object data;
		// counter to support re-entry transaction

		private int counter;

		public Connection getConnection() {
			return conn;
		}

		private void setConnection(Connection conn) {
			this.conn = conn;
		}

		public Object getTheadLocalData() {
			return data;
		}

		private void setThreadLocalData(Object data) {
			this.data = data;
		}

		public int increaseCounter() {
			return ++counter;
		}

		public int decreaseCounter() {
			return --counter;
		}

	}

	private static Logger logger = LoggerFactory.getLogger(TransactionManager.class);
	private static ThreadLocal<DaoTransContext> userThreadLocal = new ThreadLocal<DaoTransContext>();

	// this is not thread safe
	public static Connection openConnection() throws SQLException {
		return DataSourceFactory.getInstance().getConnection();
	}

	public static void closeConnection(Connection connection) {
		if (connection != null) {
			try {

				// should not happen
				if (connection.isClosed()) {
					return;
				}
				
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static void setContext(DaoTransContext context) {
		userThreadLocal.set(context);
	}

	private static void unset() {
		userThreadLocal.remove();
	}

	public static DaoTransContext getContext() {
		return userThreadLocal.get();
	}

	public static DaoTransContext transactionStart() throws SQLException {

		DaoTransContext context = getContext();
		if (context == null) {
			context = new DaoTransContext();
			Connection conn = openConnection();
			context.setConnection(conn);
			setContext(context);
		}

		context.increaseCounter();
		return context;
	}

	public static void transactionCommit() /* throws SQLException */{
		DaoTransContext context = getContext();
		if (context == null) {
			logger.warn("No transaction context available during commit, some one commit this transaction already (how it happen?)");
			// throw new SQLException ("No transaction context available during commit");
			return;
		}

		int counter = context.decreaseCounter();
		if (counter > 0) {
			// still not reach the outer boundary
			// the assumption is the caller has to have the same number of transactionCommit/Rollback calls for each tranactionStart
			return;
		}

		try {
			Connection conn = context.getConnection();
			if (conn != null) {
				conn.commit();
				closeConnection(conn);
			} else {
				logger.error("Unable to commit transaction since JDBC connection is lost (how it happen?) ");
			}
		} catch (Throwable t) {
			logger.error("Unable to commit transaction: " + t.getStackTrace());
			// throw new SQLException ("Unable to commit transaction: " + t.getStackTrace());
		} finally {
			unset();
		}

	}

	public static void transactionRollback() /* throws SQLException */{
		DaoTransContext context = getContext();
		if (context == null) {
			logger.warn("No transaction context available during rollback, some one rollback this transaction already (how it happen?)");
			// throw new SQLException ("No transaction context available during rollback");
			return;
		}

		int counter = context.decreaseCounter();
		if (counter > 0) {
			// still not reach the outer boundary
			// the assumption is the caller has to have the same number of transactionCommit/Rollback calls for each tranactionStart
			return;
		}

		try {
			Connection conn = context.getConnection();
			if (conn != null) {
				conn.rollback();
				// DaoTransManager.closeConnection(conn)(conn);
				closeConnection(conn);
			} else {
				logger.error("Unable to rollback transaction since JDBC connection is lost (how it happen?)");
			}
		} catch (Throwable t) {
			logger.error("Unable to rollback transaction: " + t.getStackTrace());
			// throw new SQLException ("Unable to rollback transaction: " + t.getStackTrace());
		} finally {
			unset();
		}
	}

	// this function is used for testing purpose
	public static void setThreadLocalData(Object data) throws SQLException {
		DaoTransContext context = getContext();
		context.setThreadLocalData(data);
	}

}
