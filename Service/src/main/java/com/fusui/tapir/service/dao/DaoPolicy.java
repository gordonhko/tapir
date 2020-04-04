package com.fusui.tapir.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoUser;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;




//CREATE TABLE "APP"."ACS_POLICIES" 
//(	"POLICY_ID" NUMBER NOT NULL ENABLE, 
//	"POLICY_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"NAME" VARCHAR2(128 BYTE), 
//	"DESCRIPTION" VARCHAR2(1000 BYTE), 
//	"ENABLED_P" NUMBER, 
//	"SYSTEM_DEFINED_CC" NUMBER(1,0) DEFAULT 0, 
//	 CONSTRAINT "ACS_POLICIES_PK" PRIMARY KEY ("POLICY_ID")
//}
//
//
//CREATE TABLE "APP"."ACS_POLICY_MASTERS" 
//(	"POLICY_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER NOT NULL ENABLE, 
//	"CREATION_DATE" DATE NOT NULL ENABLE, 
//	"SOURCE_POLICY_ID" NUMBER, 
//	 CONSTRAINT "ACS_POLICY_MASTERS_PK" PRIMARY KEY ("POLICY_MASTER_ID")
//}




public class DaoPolicy {
	private static Logger logger = LoggerFactory.getLogger(DaoPolicy.class);

	/*************************************************************************
	 * Policy
	 *************************************************************************/
	private static final String CREATE_MASTER_POLICY_TABLE_SQL = "CREATE TABLE master_policy (mid bigint, type varchar(32), action varchar(32), birthday timestamp, primary key(mid)) ";
	private static final String DROP_MASTER_POLICY_TABLE_SQL = "DROP TABLE master_policy";
	private static final String INSERT_MASTER_POLICY_SQL = "INSERT INTO master_policy (mid, type, action, birthday) VALUES (?, ?, ?, ?)";

	private static final String CREATE_POLICY_TABLE_SQL = "CREATE TABLE policy (sid bigint, mid bigint, name varchar(128), description varchar(512), status smallint, version timestamp default current_timestamp, primary key(sid)) ";
	private static final String DROP_POLICY_TABLE_SQL = "DROP TABLE policy";
	private static final String INSERT_POLICY_SQL = "INSERT INTO policy (sid, mid, name, description, status, version) VALUES (?, ?, ?, ?,  0, ?)";
	private static final String DELETE_POLICY_SQL = "INSERT INTO policy (sid, mid, name, description, status, version) VALUES (?, ?, ?, ?, -1, ?)";
	private static final String UPDATE_POLICY_SQL = "INSERT INTO policy (sid, mid, name, description, status, version) VALUES (?, ?, ?, ?,  1, ?)";

	// GKo TODO. How to get count ???
	private static final String FIND_NUMBER_OF_POLICIES_SQL = "SELECT COUNT(*) DISTINCT mid FROM policy WHERE status >= 0";

	private static final String FIND_LATEST_POLICY_BY_MASTER_ID_SQL = "SELECT TOP 1 * FROM policy WHERE mid = ? ORDER BY version DESC AND status DESC";
	private static final String FIND_ALL_POLICIES_BY_MASTER_ID_SQL = "SELECT * FROM policy WHERE mid = ? ORDER BY version DESC AND status DESC";

	// 1. find all from policy table
	// 2. app find the latest ones
	// 3. Same approach for findByName, findByDescription ...
	private static final String FIND_POLICIES_BY_ACTION_SQL = "SELECT P.*, M.type, M.action FROM policy P join master_policy M on P.mid = M.mid WHERE action=? ORDER BY version DESC";

	/*************************************************************************
	 * Policy and User/UserGroup M-M relationship
	 *************************************************************************/

	// SQL for OWNER_POLICY table
	// private static final String CREATE_MASTER_ROLE_POLICY_TABLE_SQL =
	// "CREATE TABLE master_role_policy (mid serial, user_mid bigint, policy_mid, status smallint, version timestamp default current_timestamp, primary key(sid)) ";

	// type is "User" or UserGroup
	private static final String CREATE_USER2POLICY_TABLE_SQL = "CREATE TABLE user2policy (sid bigint, user_mid bigint, policy_mid, type varchar(32), status smallint, version timestamp, primary key(sid)) ";
	private static final String DROP_USER2POLICY_TABLE_SQL = "DROP TABLE user2policy";
	private static final String INSERT_USER2POLICY_SQL = "INSERT INTO user2policy (sid, user_mid, policy_mid, type, status, version) VALUES (?, ?, ?, 'user', 0, ?)";
	private static final String DELETE_USER2POLICY_SQL = "INSERT INTO user2group (sid, user_mid, policy_mid, type, status, version) VALUES (?, ?, ?, 'user', -1, ?)";

//	private static final String CREATE_RULE2POLICY_TABLE_SQL = "CREATE TABLE rule2policy (sid bigint, rule_mid bigint, policy_mid bigint, status smallint, version timestamp, primary key(sid)) ";
//	private static final String DROP_RULE2POLICY_TABLE_SQL = "DROP TABLE rule2policy";
//	private static final String INSERT_RULE2POLICY_SQL = "INSERT INTO rule2policy (sid, rule_mid, policy_mid, status, version) VALUES (?, ?, ?, 0, ?)";

	// / GKO
	// / To be Continue ...
	// /

	// find all policies by a given userrole or app id
	private static final String FIND_POLICIES_BY_OWNER_SQL = "SELECT * FROM role_policy_table, role_policy_table WHERE role_policy_table.policy_id=role_policy_table.policy_id AND role_policy_table.policy_type=? AND role_policy_table.owner_id=? ORDER BY role_policy_table.policy_id";

	// find all policies by a given user id
	private static final String FIND_POLICIES_BY_USER_SQL = "SELECT distinct P.policy_id, P.policy_desc, P.policy_type, P.policy_actions, P.policy_rca, P.policy_category, P.system_policy FROM role_policy_table P, role_policy_table O, owneruserroletable R WHERE P.policy_type=? AND O.policy_id=P.policy_id AND O.owner_id=R.userrole_id AND R.user_id=? ORDER BY P.policy_id";
	// SELECT distinct P.policy_id, P.policy_desc, P.policy_type, P.policy_actions, P.policy_rca, P.policy_category, FROM role_policy_table P, role_policy_table O, owneruserroletable R WHERE
	// O.policy_id=P.policy_id AND O.owner_id=R.userrole_id AND R.user_id='admin'
	// "SELECT distinct P.policy_id, P.policy_desc, P.policy_type, P.policy_actions, P.policy_rca, P.policy_category, FROM role_policy_table P, role_policy_table O, owneruserroletable R WHERE P.policy_type=? AND P.policy_category=? AND O.policy_id=P.policy_id AND O.owner_id=R.userrole_id AND R.user_id=?"

	// find all policies by a given userrole id and action
	private static final String FIND_POLICIES_BY_OWNER_AND_ACTION_SQL = "SELECT * FROM role_policy_table, role_policy_table WHERE role_policy_table.policy_id=role_policy_table.policy_id AND role_policy_table.policy_type=? AND role_policy_table.policy_category=? AND role_policy_table.owner_id=? ORDER BY role_policy_table.policy_id";

	// find all policies by a given user id and action
	private static final String FIND_POLICIES_BY_USER_AND_ACTION_SQL = "SELECT distinct P.policy_id, P.policy_desc, P.policy_type, P.policy_actions, P.policy_rca, P.policy_category, P.system_policy FROM role_policy_table P, role_policy_table O, owneruserroletable R WHERE P.policy_type=? AND P.policy_category=? AND O.policy_id=P.policy_id AND O.owner_id=R.userrole_id AND R.user_id=? ORDER BY P.policy_id";
	// "SELECT distinct P.policy_id, P.policy_desc, P.policy_type, P.policy_actions, P.policy_rca, P.policy_category FROM role_policy_table P, role_policy_table O, owneruserroletable R WHERE P.policy_type=? AND P.policy_category=? AND O.policy_id=P.policy_id AND O.owner_id=R.userrole_id AND R.user_id=?"

	// find all owners by a given policy
	private static final String FIND_OWNER_IDS_BY_POLICY_SQL = "SELECT owner_id FROM role_policy_table WHERE policy_id=?";

	// unbind policies by owner id (role or app)
	private static final String DELETE_OWNER_POLICY_BY_OWNER_SQL = "DELETE from role_policy_table WHERE owner_id=?";

	private static final String DELETE_OWNER_POLICY_BY_POLICY_SQL = "DELETE from role_policy_table WHERE policy_id=?";

	private final static DaoPolicy s_singleton = new DaoPolicy();

	public static DaoPolicy getInstance() {
		return s_singleton;
	}

	private DaoPolicy() {
		// use singleton instead of generating a new object
	}

	// **********************************************************************
	// Get Policy
	// **********************************************************************

	// private VoPolicy buildPolicy(ResultSet rs) throws SQLException {
	// VoPolicy policy = new VoPolicy();
	// policy.setPolicyId(rs.getString("policy_id"));
	// policy.setPolicyDescription(rs.getString("policy_desc"));
	// policy.setPolicyType(rs.getString("policy_type"));
	// policy.setPolicyActions(rs.getString("policy_actions"));
	// policy.setPolicyRca(rs.getString("policy_rca"));
	// policy.setPolicyCategory(rs.getString("policy_category"));
	// policy.setSystemPolicy(rs.getBoolean("system_policy"));
	// return policy;
	// }
	//
	// public int findNumberOfPolicies() throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// Statement stmt = null;
	// ResultSet rs = null;
	// try {
	// stmt = conn.createStatement();
	// rs = stmt.executeQuery(FIND_NUMBER_OF_POLICIES_SQL);
	// rs.next();
	// int count = rs.getInt("count");
	// return count;
	// } catch (SQLException e) {
	// logger.info("DaoUser.countByUserId: Unable to query policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public List<VoPolicy> findPolicies(String action) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// List<VoPolicy> policyList = new ArrayList<VoPolicy>();
	//
	// try {
	// stmt = conn.prepareStatement(FIND_POLICIES_BY_ACTION_SQL);
	// stmt.setString(1, action);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// VoPolicy policy = buildPolicy(rs);
	// policyList.add(policy);
	// }
	//
	// return policyList;
	// } catch (SQLException e) {
	// logger.info("DaoUser.findAll: Unable to query all policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// /**
	// * @return VoPolicy
	// * @throws SQLException
	// */
	//
	// public VoPolicy findPolicy(String policyId) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// stmt = conn.prepareStatement(FIND_LATEST_POLICY_BY_MASTER_ID_SQL);
	// stmt.setString(1, policyId);
	// rs = stmt.executeQuery();
	// if (rs.next()) {
	// VoPolicy policy = buildPolicy(rs);
	// return policy;
	// }
	//
	// logger.warn("Cannot find policy with policyID=" + policyId);
	// return null;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// // owner id can be app id or role id
	// public List<VoPolicy> findPoliciesByOwner(String ownerId, String policyType) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// List<VoPolicy> policyList = new ArrayList<VoPolicy>();
	// stmt = conn.prepareStatement(FIND_POLICIES_BY_OWNER_SQL);
	// stmt.setString(1, policyType);
	// stmt.setString(2, ownerId);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// VoPolicy policy = buildPolicy(rs);
	// policyList.add(policy);
	// }
	//
	// return policyList;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public List<VoPolicy> findPoliciesByOwnerAndAction(String ownerId, String policyType, String action, String category) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// List<VoPolicy> policyList = new ArrayList<VoPolicy>();
	// stmt = conn.prepareStatement(FIND_POLICIES_BY_OWNER_AND_ACTION_SQL);
	// stmt.setString(1, policyType);
	// stmt.setString(2, category);
	// stmt.setString(3, ownerId);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// if (FoundryUtil.isAnyActionIn(action, rs.getString("policy_actions"))) {
	// VoPolicy policy = buildPolicy(rs);
	// policyList.add(policy);
	// }
	// }
	//
	// return policyList;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public List<VoPolicy> findPoliciesByUser(String userId) throws SQLException {
	//
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// List<VoPolicy> policyList = new ArrayList<VoPolicy>();
	// stmt = conn.prepareStatement(FIND_POLICIES_BY_USER_SQL);
	// stmt.setString(1, FoundryConstants.POLICY_TYPE_USER);
	// stmt.setString(2, userId);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// VoPolicy policy = buildPolicy(rs);
	// policyList.add(policy);
	// }
	//
	// return policyList;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public List<VoPolicy> findPoliciesByUserAndAction(String userId, String action, String category) throws SQLException {
	//
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// List<VoPolicy> policyList = new ArrayList<VoPolicy>();
	// stmt = conn.prepareStatement(FIND_POLICIES_BY_USER_AND_ACTION_SQL);
	// stmt.setString(1, FoundryConstants.POLICY_TYPE_USER);
	// stmt.setString(2, category);
	// stmt.setString(3, userId);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// if (FoundryUtil.isAnyActionIn(action, rs.getString("policy_actions"))) {
	// VoPolicy policy = buildPolicy(rs);
	// policyList.add(policy);
	// }
	// }
	//
	// return policyList;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// // owner id can be app id or role id
	// public List<String> findPolicyIdsByOwner(String ownerId, String policyType) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// List<String> policyIdList = new ArrayList<String>();
	// stmt = conn.prepareStatement(FIND_POLICIES_BY_OWNER_SQL);
	// stmt.setString(1, policyType);
	// stmt.setString(2, ownerId);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// String policyId = rs.getString("policy_id");
	// policyIdList.add(policyId);
	// }
	//
	// return policyIdList;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// // **********************************************************************
	// // Insert, Update and Delete Policy
	// // **********************************************************************
	//
	// // public VoPolicy addPolicy(String policyId, String policyDescription, String actions, String policyType) throws SQLException {
	// // VoPolicy policy = new VoPolicy();
	// // policy.setPolicyId(policyId);
	// // policy.setpolicyDescription(policyDescription);
	// // policy.setActions(actions);
	// // policy.setpolicyType(policyType);
	// // return addPolicy (policy);
	// // }
	//
	// public VoPolicy addPolicy(VoPolicy policy) throws SQLException {
	//
	// if (FoundryUtil.isEmpty(policy.getPolicyId()) || FoundryUtil.isEmpty(policy.getPolicyActions()) || FoundryUtil.isEmpty(policy.getPolicyType())
	// || FoundryUtil.isEmpty(policy.getPolicyRca()) || FoundryUtil.isEmpty(policy.getPolicyCategory())) {
	// throw new SQLException("Missing data when creating a policy");
	// }
	//
	// DaoTransContext context = TransactionManager.getContext();
	// Connection conn = context.getConnection();
	// PreparedStatement stmt = null;
	//
	// // for debug purpose
	// if (null != context.getTheadLocalData()) {
	// policy.setPolicyDescription(context.getTheadLocalData() + ":" + policy.getPolicyDescription());
	// }
	//
	// try {
	// stmt = conn.prepareStatement(INSERT_POLICY_SQL);
	// stmt.setString(1, policy.getPolicyId());
	// stmt.setString(2, policy.getPolicyDescription());
	// stmt.setString(3, policy.getPolicyType());
	// stmt.setString(4, policy.getPolicyActions());
	// stmt.setString(5, policy.getPolicyRca());
	// stmt.setString(6, policy.getPolicyCategory());
	// stmt.setBoolean(7, policy.getSystemPolicy());
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// throw new SQLException("Unable to insert policy since policyId mismatched : " + policy.getPolicyId());
	// }
	//
	// return policy;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to insert policy into policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// /*
	// * return primary key
	// */
	// public VoPolicy updatePolicy(String policyId, String policyDescription, String policyType, String actions, String rca, String category) throws SQLException {
	// VoPolicy policy = new VoPolicy();
	// policy.setPolicyId(policyId);
	// policy.setPolicyDescription(policyDescription);
	// policy.setPolicyType(policyType);
	// policy.setPolicyActions(actions);
	// policy.setPolicyRca(rca);
	// policy.setPolicyCategory(category);
	// return updatePolicy(policy);
	// }
	//
	// public VoPolicy updatePolicy(VoPolicy policy) throws SQLException {
	//
	// if (FoundryUtil.isEmpty(policy.getPolicyId()) || FoundryUtil.isEmpty(policy.getPolicyActions()) || FoundryUtil.isEmpty(policy.getPolicyType())
	// || FoundryUtil.isEmpty(policy.getPolicyRca())) {
	// throw new SQLException("Missing data when updating a policy");
	// }
	//
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// try {
	// stmt = conn.prepareStatement(UPDATE_POLICY_SQL);
	// stmt.setString(1, policy.getPolicyDescription());
	// // stmt.setString(2, policy.getPolicyType());
	// stmt.setString(2, policy.getPolicyActions());
	// stmt.setString(3, policy.getPolicyRca());
	// stmt.setString(4, policy.getPolicyCategory());
	// stmt.setString(5, policy.getPolicyId());
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// throw new SQLException("Unable to update policy since policyId mismatched : " + policy.getPolicyId());
	// }
	// return policy;
	//
	// } catch (SQLException e) {
	// logger.info("Unable to update policy table: " + policy.getPolicyId() + " msg=" + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public void deletePolicy(String policyId) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// try {
	// stmt = conn.prepareStatement(DELETE_POLICY_SQL);
	// stmt.setString(1, policyId);
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// logger.warn("Unable to delete policy from policy table policyId=" + policyId);
	// }
	//
	// } catch (SQLException e) {
	// logger.error("Unable to delete policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// // **********************************************************************
	// // Role and Policy Relationships
	// // **********************************************************************
	//
	// // public void bindRolePolicy(String roleId, String policyId ) throws SQLException {
	// // bindOwnerPolicy (roleId, policyId);
	// // }
	// //
	// // public void bindAppPolicy(String appId, String policyId ) throws SQLException {
	// // bindOwnerPolicy (appId, policyId);
	// // }
	//
	// public void bindOwnerPolicy(String ownerId, String policyId, boolean enable) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// try {
	// stmt = conn.prepareStatement(INSERT_USER2POLICY_SQL);
	// stmt.setString(1, ownerId);
	// stmt.setString(2, policyId);
	// stmt.setString(3, enable ? FoundryConstants.POLICY_ENABLED : FoundryConstants.POLICY_DISABLED);
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// throw new SQLException("Unable to insert ownerpolicy table since mismatched : onwerId=" + ownerId + " policyId=" + policyId);
	// }
	//
	// } catch (SQLException e) {
	// logger.error("Unable to insert into ownerpolicy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// // public void unbindRolePolicy(String roleId, String policyId) throws SQLException {
	// // unbindOwnerPolicy (roleId, policyId, VoPolicyConst.POLICY_TYPE_USER);
	// // }
	// //
	// // public void unbinAppPolicy(String appId, String policyId ) throws SQLException {
	// // unbindOwnerPolicy (appId, policyId, VoPolicyConst.POLICY_TYPE_APP);
	// // }
	//
	// public void unbindOwnerPolicy(String ownerId, String policyId) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// try {
	// stmt = conn.prepareStatement(DELETE_USER2POLICY_SQL);
	// stmt.setString(1, ownerId);
	// stmt.setString(2, policyId);
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// throw new SQLException("Unable to delete ownerpolicy table since mismatched : onwerId=" + ownerId + " policyId=" + policyId);
	// }
	//
	// } catch (SQLException e) {
	// logger.error("Unable to delete from ownerpolicy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public void unbindPoliciesByOwner(String ownerId) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// try {
	// stmt = conn.prepareStatement(DELETE_OWNER_POLICY_BY_OWNER_SQL);
	// stmt.setString(1, ownerId);
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// // throw new SQLException("Unable to delete owneruserrole table since mismatched : userId=" + userId + " roleId=" + userroleId);
	// }
	//
	// } catch (SQLException e) {
	// logger.error("Unable to delete owner (role or app) from owneruserrole table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public void unbindOwnersByPolicy(String policyId) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// try {
	// stmt = conn.prepareStatement(DELETE_OWNER_POLICY_BY_POLICY_SQL);
	// stmt.setString(1, policyId);
	// int nRows = stmt.executeUpdate();
	// if (nRows == 0) {
	// // throw new SQLException("Unable to delete owneruserrole table since mismatched : userId=" + userId + " roleId=" + userroleId);
	// }
	//
	// } catch (SQLException e) {
	// logger.error("Unable to delete owner (role or app) from owneruserrole table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }
	//
	// public List<String> findOwnerIdsByPolicy(String policyId) throws SQLException {
	// Connection conn = TransactionManager.getContext().getConnection();
	// PreparedStatement stmt = null;
	// ResultSet rs = null;
	// try {
	// List<String> ownerList = new ArrayList<String>();
	// stmt = conn.prepareStatement(FIND_OWNER_IDS_BY_POLICY_SQL);
	// stmt.setString(1, policyId);
	// rs = stmt.executeQuery();
	// while (rs.next()) {
	// String id = rs.getString("owner_id");
	// ownerList.add(id);
	// }
	// return ownerList;
	//
	// } catch (SQLException e) {
	// logger.error("Unable to get policy from policy table: " + e.getMessage());
	// throw e;
	// } finally {
	// try {
	// if (rs != null) {
	// rs.close();
	// }
	// if (stmt != null) {
	// stmt.close();
	// }
	// // if (conn != null) {
	// // DaoTransManager.closeConnection(conn)(conn);
	// // }
	// } catch (SQLException e1) {
	// logger.error("Unable to close statement or connection: " + e1.getMessage());
	// throw e1;
	// }
	// }
	// }

	public VoPolicy createPolicy(VoPolicy policy) throws SQLException {

		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		// 1. get unique number for mid and time stamp
		long mid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());

		// Timestamp ts = rs.getTimestamp(colnum); // column is TIMESTAMP
		// return ts !=null ? new Date(ts.getTime) : null;

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_MASTER_POLICY_SQL);
			stmt.setLong(1, mid);
			stmt.setString(2, policy.getType());
			stmt.setString(3, policy.getAction());
			stmt.setTimestamp(4, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert master policy : " + policy);
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
			stmt = conn.prepareStatement(INSERT_POLICY_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, mid);
			stmt.setString(3, policy.getName());
			stmt.setString(4, policy.getDescription());
			// stmt.setLong(5, FoundryConstants.STATUS_CREATED); // 0
			stmt.setTimestamp(5, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert group : " + policy);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		policy.setMid(mid);
		policy.setBirthday(now);
		policy.setSid(sid);
		policy.setStatus(FoundryConstants.STATUS_CREATED);
		policy.setVersion(now);
		return policy;

	}



}
