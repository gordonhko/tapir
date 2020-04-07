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


//CREATE TABLE "APP"."ACS_GRANTS" 
//(	"GRANT_ID" NUMBER NOT NULL ENABLE, 
//	"POLICY_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"USER_ID" NUMBER, 
//	"GROUP_MASTER_ID" NUMBER, 
//	 CONSTRAINT "ACS_GRANTS_PK" PRIMARY KEY ("GRANT_ID")
//}


public class DaoGrant {
	private static Logger logger = LoggerFactory.getLogger(DaoGrant.class);

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

	private final static DaoGrant instance = new DaoGrant();

	public static DaoGrant getInstance() {
		return instance;
	}

	private DaoGrant() {
		// use singleton instead of generating a new object
	}

	
	
}
