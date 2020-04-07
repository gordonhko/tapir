package com.fusui.tapir.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.dto.VoGroup;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;
import com.fusui.tapir.service.dao.SqlDictionary.SqlKey;

/*
 * @author gko
 */

public class DaoGroup {
	private static Logger logger = LoggerFactory.getLogger(DaoGroup.class);

//	private static final String SQL_CREATE_MASTER_GROUPS = 
//		"CREATE TABLE TAPIR_GROUP_MASTERS ( "+
//			"GROUP_MASTER_ID NUMBER NOT NULL, "+
//			"TENANT_ID NUMBER NOT NULL, "+
//			"CREATOR NUMBER, " +
//			"CREATION_DATE DATE, "+ 
//			"PRIMARY KEY (GROUP_MASTER_ID) )";
//			
//	private static final String SQL_DROP_MASTER_GROUPS = "DROP TABLE TAPIR_GROUP_MASTERS";
//
//	private static final String CREATE_GROUP_TABLE_SQL = 
//		"CREATE TABLE TAPIR_GROUPS ( " + 
//			"GROUP_ID NUMBER NOT NULL, " +
//			"GROUP_MASTER_ID NUMBER NOT NULL, " +
//			"TENANT_ID NUMBER NOT NULL, " +
//			"CREATOR NUMBER NOT NULL, " +
//			"CREATION_DATE DATE NOT NULL," + 
//			"DELETED BOOLEAN, " +
//			"NAME VARCHAR2(128), " +
//			"DESCRIPTION VARCHAR2(1024), " + 
//			"ENABLED BOOLEAN, " +
//			"PRIMARY KEY (GROUP_ID) )";
//			
//	private static final String DROP_GROUP_TABLE_SQL = "DROP TABLE TAPIR_GROUPS";
//			
//	private static final String APPEND_MASTER_GROUP_SQL = "INSERT INTO master_group (GROUP_MASTER_ID, TENANT_ID, CREATOR, CREATION_DATE) VALUES (?, ?, ?, ?)";
//	private static final String APPEND_GROUP_SQL = "INSERT INTO grouptable (GROUP_ID, GROUP_MASTER_ID, TENANT_ID, CREATOR, CREATION_DATE, DELETED, NAME, DESCRIPTION, ENABLED) VALUES (?, ?, ?, ?, ?, false, ?, ?, ?)";
//	private static final String DELETE_GROUP_SQL = "INSERT INTO grouptable (GROUP_ID, GROUP_MASTER_ID, TENANT_ID, CREATOR, CREATION_DATE, DELETED) VALUES (?, ?, ?, ?, ?, true)";

	
	private final static DaoGroup s_singleton = new DaoGroup();

	public static DaoGroup getInstance() {
		return s_singleton;
	}

	private DaoGroup() {
		// use singleton instead of generating a new object
	}

	// **********************************************************************
	// Insert, Update and Delete Policy
	// **********************************************************************
	 
	public Long createGroup(Long userId, Long tenantId, String name, String desc, Boolean enabled) throws SQLException {

		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		Timestamp ts = null;
		PreparedStatement stmt = null;
	
		try {
			long masterId = DaoSequence.getInstance().getUniqueId();		// get unique id
			ts = new Timestamp(new Date().getTime());
			stmt = conn.prepareStatement(SqlDictionary.getInstance().getSqlStmt(SqlKey.APPEND_MASTER_GROUPS_SQL) );
			stmt.setLong(1, masterId);
			stmt.setLong(2, tenantId);
			stmt.setLong(3, userId);
			stmt.setTimestamp(4, ts);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert master group : " + name);
			}
			stmt.close();
			
			stmt = null;
			long id = DaoSequence.getInstance().getUniqueId();
			stmt = conn.prepareStatement(SqlDictionary.getInstance().getSqlStmt(SqlKey.APPEND_GROUPS_SQL));
			stmt.setLong(1, id);
			stmt.setLong(2, masterId);		// master ID;
			stmt.setLong(3, tenantId);
			stmt.setLong(4, userId);
			stmt.setTimestamp(5, ts);
			
			stmt.setString(6, name);
			stmt.setString(7, desc);
			stmt.setBoolean(8, enabled);
			 
			nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert group : " + name);
			}
			return id;	
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	
	/*
	 * return primary key
	 */
	public Long updateGroup(Long userId, Long tenantId, Long groupId, String name, String desc, Boolean enabled) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		Timestamp ts = new Timestamp(new Date().getTime());
		PreparedStatement stmt = null;		

		try {
			long sid = DaoSequence.getInstance().getUniqueId();
			stmt = conn.prepareStatement(SqlDictionary.getInstance().getSqlStmt(SqlKey.APPEND_GROUPS_SQL));
			stmt.setLong(1, sid);
			stmt.setLong(2, groupId);		// master ID;
			stmt.setLong(3, tenantId);
			stmt.setLong(4, userId);
			stmt.setTimestamp(5, ts);
			
			stmt.setString(6,name);
			stmt.setString(7, desc);
			stmt.setBoolean(8, enabled);
			 
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert group : " + name);
			}
			return sid;
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public Long deleteGroup(Long userId, Long tenantId, VoGroup group) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		Timestamp ts = new Timestamp(new Date().getTime());
		PreparedStatement stmt = null;		

		try {
			long sid = DaoSequence.getInstance().getUniqueId();
			stmt = conn.prepareStatement(SqlDictionary.getInstance().getSqlStmt(SqlKey.DELETE_GROUPS_SQL));
			stmt.setLong(1, sid);
			stmt.setLong(2, group.getId());		// master ID;
			stmt.setLong(3, tenantId);
			stmt.setLong(4, userId);
			stmt.setTimestamp(5, ts);

			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to delete group : " + group);
			}
			return sid;
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	public List <RowGroup> readGroup(Long userId, Long tenantId ) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;	
		List<RowGroup> result = new ArrayList<RowGroup>();

		try {
			stmt = conn.prepareStatement(SqlDictionary.getInstance().getSqlStmt(SqlKey.SELECT_GROUPS_SQL));
			stmt.setLong(1, tenantId);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				RowGroup row = buildRow(rs);
				result.add(row);
			}

			return result;
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	// SELECT GROUP_ID, GROUP_MASTER_ID, CREATOR, CREATION_DATE, DELETED, NAME, DESCRIPTION, ENABLED WHERE TENANT_ID = ?
	private RowGroup buildRow(ResultSet rs) throws SQLException {
		RowGroup group = new RowGroup();
		group.setId(rs.getLong(1));
		group.setMasterId(rs.getLong(2));
		group.setCreator(rs.getLong(3));
		group.setTimeStamp(rs.getDate(4));
		group.setDeleted(rs.getBoolean(5));
		group.setName(rs.getString(6));
		group.setDescription(rs.getString(7));
		group.setEnabled(rs.getBoolean(8));
		return group;
	}

	
	

}
