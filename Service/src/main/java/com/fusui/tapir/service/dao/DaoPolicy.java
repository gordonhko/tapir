package com.fusui.tapir.service.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




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




}
