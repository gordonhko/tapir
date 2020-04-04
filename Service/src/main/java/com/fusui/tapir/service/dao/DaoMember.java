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

import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoApp;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;

//CREATE TABLE "APP"."ACS_GROUPS_USERS" 
//(	"GROUP_USER_ID" NUMBER NOT NULL ENABLE, 
//	"GROUP_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"USER_ID" NUMBER NOT NULL ENABLE, 
//	 CONSTRAINT "ACS_GROUPS_USERS_PK" PRIMARY KEY ("GROUP_USER_ID")
//}

public class DaoMember {
	private static Logger logger = LoggerFactory.getLogger(DaoMember.class);

	/*************************************************************************
	 * Policy
	 *************************************************************************/
	private static final String CREATE_MASTER_APP_TABLE_SQL = "CREATE TABLE master_app (mid bigint, parent_mid bigint, type varchar(32), max_copy smallint, birthday timestamp, deathday timestamp, primary key(mid)) ";
	private static final String DROP_MASTER_APP_TABLE_SQL = "DROP TABLE master_app";
	private static final String INSERT_MASTER_APP_SQL = "INSERT INTO master_app (mid, parent_mid, type, max_copy, birthday, deathday) VALUES (?, ?, ?, ?, ?, ?)";

	private static final String CREATE_APP_TABLE_SQL = "CREATE TABLE app (sid bigint, mid bigint, name varchar(128), description varchar(512), owner bigint, status smallint, version timestamp, primary key(sid)) ";
	private static final String DROP_APP_TABLE_SQL = "DROP TABLE app";
	private static final String INSERT_APP_SQL = "INSERT INTO app (sid, mid, name, description, owner, status, version) VALUES (?, ?, ?, ?, ?,  0, ?)";
	private static final String DELETE_APP_SQL = "INSERT INTO app (sid, mid, name, description, owner, status, version) VALUES (?, ?, ?, ?, ?, -1, ?)";
	private static final String UPDATE_APP_SQL = "INSERT INTO app (sid, mid, name, description, owner, status, version) VALUES (?, ?, ?, ?, ?,  1, ?)";

	// GKo TODO. How to get count ???
	private static final String FIND_NUMBER_OF_APP_SQL = "SELECT COUNT(*) DISTINCT mid FROM app WHERE status >= 0";

	private static final String FIND_APP_BY_MID_SQL = "SELECT * FROM app join master_app on app.mid=master_app.mid WHERE app.mid = ? ORDER BY version DESC, status DESC LIMIT 1";
	private static final String FIND_ALL_APPS_SQL = "SELECT * FROM app join master_app on app.mid=master_app.mid WHERE app.sid in (SELECT MAX(sid) FROM app group by (app.mid) ) ORDER BY status DESC";

	// 1. get all from app table
	// 2. find the latest ones
	// 3. Same approach for findByName, findByDescription ...
	private static final String FIND_APPS_BY_TYPE_SQL = "SELECT A.*, M.type FROM app A join master_group M on A.mid = M.mid WHERE type=? ORDER BY version DESC";

	/*************************************************************************
	 * App and Group M2M relationship
	 *************************************************************************/
	// type is dummy so far
	private static final String CREATE_APP2GROUP_TABLE_SQL = "CREATE TABLE app2group (sid bigint, app_mid bigint, group_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid)) ";
	private static final String DROP_APP2GROUP_TABLE_SQL = "DROP TABLE app2group";
	private static final String INSERT_APP2GROUP_SQL = "INSERT INTO app2group (sid, app_mid, group_mid, type, status, version) VALUES (?, ?, ?, ?, 0, ?)";
	private static final String DELETE_APP2GROUP_SQL = "INSERT INTO app2group (sid, app_mid, group_mid, type, status, version) VALUES (?, ?, ?, ?, -1, ?)";

	private final static DaoMember instance = new DaoMember();

	public static DaoMember getInstance() {
		return instance;
	}

	private DaoMember() {
		// use singleton instead of generating a new object
	}

	// **********************************************************************
	// Insert, Update and Delete Policy
	// **********************************************************************

	public VoApp createApp(VoApp app) throws SQLException {

		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		// 1. get unique number for mid and time stamp
		long mid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());
		Date dDate = app.getDeathday();
		
		Timestamp deathDay = new Timestamp(app.getDeathday().getTime());

		// Timestamp ts = rs.getTimestamp(colnum); // column is TIMESTAMP
		// return ts !=null ? new Date(ts.getTime) : null;

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_MASTER_APP_SQL);
			stmt.setLong(1, mid);
			stmt.setLong(2, app.getParentId());
			stmt.setString(3, app.getType());
			stmt.setInt(4,  app.getMaxCopy());
			stmt.setTimestamp(5, now);
			stmt.setTimestamp(6, deathDay );
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert master app : " + app);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		// 3. create slave group
		// sid, mid, name, desc, status, version
		stmt = null;
		long sid = DaoSequence.getInstance().getUniqueId();
		try {
			stmt = conn.prepareStatement(INSERT_APP_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, mid);
			stmt.setString(3, app.getName());
			stmt.setString(4, app.getDesc());
			stmt.setLong(5, app.getOwner());
			// stmt.setLong(5, FoundryConstants.STATUS_CREATED); // 0
			stmt.setTimestamp(6, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert app : " + app);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		app.setMid(mid);
		app.setBirthday(now);
		app.setSid(sid);
		app.setStatus(FoundryConstants.STATUS_CREATED);
		app.setVersion(now);
		return app;

	}
	
	public void bindAppByGroup(long appMid, long groupMid) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;


		// 1. get unique number for mid and time stamp
		long sid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_APP2GROUP_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, appMid);
			stmt.setLong(3, groupMid);
			stmt.setString(4,  "");
			stmt.setTimestamp(5, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert user2group appId="+appMid+", groupId="+groupMid);
			}
		}
		finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	private VoApp buildApp(ResultSet rs) throws SQLException {
		VoApp app = new VoApp();
		app.setMid(rs.getLong("mid"));
		app.setParentId(rs.getLong("parent_mid"));
		app.setType(rs.getString("type"));
		app.setMaxCopy(rs.getInt("max_copy"));
		Timestamp ts = rs.getTimestamp("birthday");
		app.setBirthday(new Date(ts.getTime()));
		
		ts = rs.getTimestamp("deathday");
		app.setDeathday(new Date(ts.getTime()));
		
		
		//sid, mid, name, description, owner, status, version
		app.setSid(rs.getLong("sid"));
		app.setName(rs.getString("name"));
		app.setDesc(rs.getString("description"));
		app.setOwner(rs.getLong("owner"));
		app.setStatus(rs.getInt("status"));
		ts = rs.getTimestamp("version");
		app.setVersion (new Date(ts.getTime()));
		
		return app;
	}
	
	public VoApp getAppByMid(long appId) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		try {
			VoApp app = null;
			stmt = conn.prepareStatement(FIND_APP_BY_MID_SQL);
			stmt.setLong(1, appId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				app = buildApp(rs);
			}
			return app;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public List<VoApp> getAllApps() throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;
		List <VoApp> aList = new ArrayList <VoApp>();
		try {
			VoApp app = null;
			stmt = conn.prepareStatement(FIND_ALL_APPS_SQL);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				app = buildApp(rs);
				aList.add(app);
			}
			return aList;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	
	
	
	
	
}
