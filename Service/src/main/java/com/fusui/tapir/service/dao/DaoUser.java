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
import com.fusui.tapir.common.dto.VoGroup;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoUser;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;

public class DaoUser {
	private static Logger logger = LoggerFactory.getLogger(DaoUser.class);
	public static String USER_STATUS_ACTIVE = "active";
	public static String USER_STATUS_INACTIVE = "inactive";

	private final static DaoUser s_singleton = new DaoUser();

	public static DaoUser getInstance() {
		return s_singleton;
	}

	private DaoUser() {
		// use singleton instead of generating a new object
	}

	// **********************************************************************
	// DDL
	// **********************************************************************

	/*************************************************************************
	 * Policy
	 *************************************************************************/
	private static final String CREATE_MASTER_USER_TABLE_SQL = "CREATE TABLE master_user (mid bigint, birthday timestamp, primary key(mid)) ";
	private static final String DROP_MASTER_USER_TABLE_SQL = "DROP TABLE master_user";
	private static final String INSERT_MASTER_USER_SQL = "INSERT INTO master_user (mid, birthday) VALUES (?, ?)";

	private static final String CREATE_USER_TABLE_SQL = "CREATE TABLE usertable (sid bigint, mid bigint, first_name varchar(128), last_name varchar(128), login_name varchar(256), password varchar(256), department varchar(256), note varchar (512), active smallint, status smallint, version timestamp, primary key(sid)) ";
	private static final String DROP_USER_TABLE_SQL = "DROP TABLE usertable";
	private static final String INSERT_USER_SQL = "INSERT INTO usertable (sid, mid, first_name, last_name, login_name, password, department, note, active, status, version) VALUES (?, ?, ?, ? ,? ,? ,? ,? ,?  ,0, ?)";
	private static final String DELETE_USER_SQL = "INSERT INTO usertable (sid, mid, first_name, last_name, login_name, password, department, note, active, status, version) VALUES (?, ?, ?, ? ,? ,? ,? ,? ,?  ,1, ?)";
	private static final String UPDATE_USER_SQL = "INSERT INTO usertable (sid, mid, first_name, last_name, login_name, password, department, note, active, status, version) VALUES (?, ?, ?, ? ,? ,? ,? ,? ,? ,-1, ?)";

	// GKo TODO. How to get count ???
	private static final String FIND_NUMBER_OF_POLICIES_SQL = "SELECT COUNT(*) DISTINCT mid FROM usertable WHERE status >= 0";

	private static final String FIND_USER_BY_MID_SQL = "SELECT * FROM usertable join master_user on usertable.mid=master_user.mid WHERE usertable.mid = ? ORDER BY version DESC, status DESC LIMIT 1";
	private static final String FIND_ALL_USERS_SQL = "SELECT * FROM usertable join master_user on usertable.mid=master_user.mid WHERE usertable.sid in (SELECT max (sid) FROM usertable group by (usertable.mid) ) ORDER BY status DESC";
	
	// 1. find all from policy table
	// 2. app find the latest ones
	// 3. Same approach for findByName, findByDescription ...
	private static final String FIND_USERS_BY_DEPARTMENT_SQL = "SELECT U.*, FROM usertable U join master_user M on U.mid = M.mid WHERE department=? ORDER BY version DESC, status DESC";

	private static final String FIND_USER_BY_LOGIN_NAME_SQL = "SELECT U.*, M.* FROM usertable U join master_user M on U.mid = M.mid WHERE login_name=? ORDER BY version DESC, status DESC";

	/*************************************************************************
	 * User and Group M2M relationship
	 *************************************************************************/
	// type is dummy so far
	private static final String CREATE_USER2GROUP_TABLE_SQL = "CREATE TABLE user2group (sid bigint, user_mid bigint, group_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid)) ";
	private static final String DROP_USER2GROUP_TABLE_SQL = "DROP TABLE user2group";
	private static final String INSERT_USER2GROUP_SQL = "INSERT INTO user2group (sid, user_mid, group_mid, type, status, version) VALUES (?, ?, ?, ?, 0, ?)";
	private static final String DELETE_USER2GROUP_SQL = "INSERT INTO user2group (sid, user_mid, group_mid, type, status, version) VALUES (?, ?, ?, ?, -1, ?)";

	/*************************************************************************
	 * User and Policy M2M relationship
	 *************************************************************************/
	// type is "User" or UserGroup
	private static final String CREATE_USER2POLICY_TABLE_SQL = "CREATE TABLE user2policy (sid bigint, user_mid bigint, policy_mid bigint, type varchar(32), status smallint, version timestamp, primary key(sid)) ";
	private static final String DROP_USER2POLICY_TABLE_SQL = "DROP TABLE user2policy";
	private static final String INSERT_USER2POLICY_SQL = "INSERT INTO user2policy (sid, user_mid, policy_mid, type, status, version) VALUES (?, ?, ?, ?, 0, ?)";
	private static final String DELETE_USER2POLICY_SQL = "INSERT INTO user2group (sid, user_mid, policy_mid, type, status, version) VALUES (?, ?, ?, '?, -1, ?)";

	private static final String FIND_POLICIES_BY_USER_MID_SQL = 
			"SELECT P.*, M.* FROM (policy P join master_policy M on P.mid = M.mid) join user2policy U2P on M.mid = U2P.policy_mid WHERE U2P.user_mid=? AND M.action=? ORDER BY version DESC, status DESC";

	/*************************************************************************
	 * User 
	 *************************************************************************/

	public VoUser createUser(VoUser user) throws SQLException {

		// if (StringUtils.isEmpty(group.getName()) || StringUtils.isEmpty(group.getType())) {
		// throw new SQLException("Missing data when creating a group");
		// }

		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		// 1. get unique number for mid and time stamp
		long mid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_MASTER_USER_SQL);
			stmt.setLong(1, mid);
			stmt.setTimestamp(2, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert master user : " + user);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		// 3. create slave group
		// (sid, mid, first_name, last_name, login_name, password, department, note, active, status, version) VALUES (?, ?, ?, ? ,? ,? ,? ,? ,? ,0, ?)";

		stmt = null;
		long sid = DaoSequence.getInstance().getUniqueId();
		try {
			stmt = conn.prepareStatement(INSERT_USER_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, mid);
			stmt.setString(3, user.getFirstName());
			stmt.setString(4, user.getLastName());
			stmt.setString(5, user.getLoginName());
			stmt.setString(6, user.getPassword());
			stmt.setString(7, user.getDepartment());
			stmt.setString(8, user.getNote());
			stmt.setInt(9, user.getActive());

			// stmt.setLong(x, FoundryConstants.STATUS_CREATED); // 0
			stmt.setTimestamp(10, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert user : " + user);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		user.setMid(mid);
		user.setBirthday(now);
		user.setSid(sid);
		user.setStatus(FoundryConstants.STATUS_CREATED);
		user.setVersion(now);
		return user;

	}
 
	private VoUser buildUser(ResultSet rs) throws SQLException {
		VoUser user = new VoUser();
		user.setMid(rs.getLong("mid"));
		Timestamp ts = rs.getTimestamp("birthday");
		user.setBirthday(new Date(ts.getTime()));
		
		user.setSid(rs.getLong("sid"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setLoginName(rs.getString("login_name"));
		
		user.setPassword(rs.getString("password"));
		user.setDepartment(rs.getString("department"));
		user.setNote(rs.getString("note"));
		
		user.setActive(rs.getInt("active"));
		user.setStatus(rs.getInt("status"));
		ts = rs.getTimestamp("version");
		user.setVersion (new Date(ts.getTime()));
		
		return user;
	}
	

	public VoUser getUserByMid(long userId) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		try {
			VoUser user = null;
			stmt = conn.prepareStatement(FIND_USER_BY_MID_SQL);
			stmt.setLong(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user = buildUser(rs);
			}
			return user;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	public VoUser getUserByLoginName(String loginName) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		try {
			VoUser user = null;
			stmt = conn.prepareStatement(FIND_USER_BY_LOGIN_NAME_SQL);
			stmt.setString(1, loginName);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				user = buildUser(rs);
			}
			return user;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}


	/*************************************************************************
	 * User and Group
	 *************************************************************************/
	
	public void bindUserByGroup(long userMid, long groupMid) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		// 1. get unique number for mid and time stamp
		long sid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_USER2GROUP_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, userMid);
			stmt.setLong(3, groupMid);
			stmt.setString(4, "");
			stmt.setTimestamp(5, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert user2group userId=" + userMid + ", groupId=" + groupMid);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public List<VoGroup> getAllGroupsByUser(long userId) {
		// TODO Auto-generated method stub
		return null;
	}
	

	/*************************************************************************
	 * User and Policy
	 * @throws SQLException 
	 *************************************************************************/
	
	// sid, mid, name, description, status, version
	private VoPolicy buildPolicy(ResultSet rs) throws SQLException {
		VoPolicy policy = new VoPolicy();
		policy.setMid(rs.getLong("mid"));
		policy.setType(rs.getString("type"));
		policy.setAction(rs.getString("action"));
		Timestamp ts = rs.getTimestamp("birthday");
		policy.setBirthday(new Date(ts.getTime()));
		
		policy.setSid(rs.getLong("sid"));
		policy.setName(rs.getString("name"));
		policy.setDescription(rs.getString("description"));
		policy.setStatus(rs.getInt("status"));
		ts = rs.getTimestamp("version");
		policy.setVersion (new Date(ts.getTime()));
		
		return policy;
	}
	
	public List<VoPolicy> getAllPoliciesByUserWithGroupAndAction(long userId, String action) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		try {

			List <VoPolicy> pList = new ArrayList <VoPolicy>();
			VoPolicy policy = null;
			stmt = conn.prepareStatement(FIND_POLICIES_BY_USER_MID_SQL);
			stmt.setLong (1, userId);
			stmt.setString(2, action);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				policy = buildPolicy(rs);
				pList.add(policy);
			}
			return pList;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void bindUserByPolicy(long userMid, long policyMid) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		// 1. get unique number for mid and time stamp
		long sid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_USER2POLICY_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, userMid);
			stmt.setLong(3, policyMid);
			stmt.setString(4, "");
			stmt.setTimestamp(5, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert user2group userId=" + userMid + ", groupId=" + policyMid);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
		
	}

	

	
	
}
