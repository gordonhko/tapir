package com.fusui.tapir.service.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;
import com.fusui.tapir.service.dao.SqlDictionary.SqlKey;

// Postgres manual
// http://www.postgresql.org/docs/8.1/static/sql-createsequence.html
public class DaoPgSequence extends DaoSequence {

	private static Logger logger = LoggerFactory.getLogger(DaoPgSequence.class);
	private static final DaoPgSequence s_singleton = new DaoPgSequence();

	public static DaoPgSequence getInstance() {
		return s_singleton;
	}

	protected DaoPgSequence() {
		// use singleton instead of generating a new object
	}

	
	@Override
	protected long getNextNumber() throws SQLException {

		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(SqlDictionary.getInstance().getSqlStmt(SqlKey.SELECT_SEQUENCE_SQL));
			rs.next();
			int count = rs.getInt(1);
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
