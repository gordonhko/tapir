package com.fusui.tapir.service.dao;

import java.nio.ByteBuffer;
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

import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;


//CREATE TABLE "APP"."ACS_RULE_MASTERS" 
//(	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	 CONSTRAINT "ACS_RULE_MASTERS_PK" PRIMARY KEY ("RULE_MASTER_ID")
//}
//
//
//
//CREATE TABLE "APP"."ACS_RULES" 
//(	"RULE_ID" NUMBER NOT NULL ENABLE, 
//	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"POLICY_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"NAME" VARCHAR2(128 BYTE), 
//	"DESCRIPTION" VARCHAR2(1000 BYTE), 
//	"TYPE_NAME" VARCHAR2(32 BYTE), 
//	"ENABLED_P" NUMBER(1,0) DEFAULT 1, 
//	 CONSTRAINT "ACS_RULES_PK" PRIMARY KEY ("RULE_ID")
//}
//
//
//
//
//CREATE TABLE "APP"."ACS_RULES_ACTIONS" 
// (	"RULE_ACTION_ID" NUMBER NOT NULL ENABLE, 
//	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"ACTION_NAME" VARCHAR2(32 BYTE) NOT NULL ENABLE, 
//	 CONSTRAINT "ACS_RULES_ACTIONS_PK" PRIMARY KEY ("RULE_ACTION_ID")
//}
// 
// 
// 
// CREATE TABLE "APP"."ACS_RULES_ATTRIBUTES" 
// (	"RULE_ATTRIBUTE_ID" NUMBER NOT NULL ENABLE, 
//	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"ATTR_NAME" VARCHAR2(32 BYTE) NOT NULL ENABLE, 
//	"ATTR_VALUE" VARCHAR2(1000 BYTE) NOT NULL ENABLE, 
//	 CONSTRAINT "ACS_RULES_ATTRIBUTES_PK" PRIMARY KEY ("RULE_ATTRIBUTE_ID") 
//}
//CREATE TABLE "APP"."ACS_RULES_CUST_ATTR_CRITERIA" 
//(	"RULES_CUST_ATTR_CRITERIA_ID" NUMBER NOT NULL ENABLE, 
//	"CUSTOM_ATTRIBUTE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER NOT NULL ENABLE, 
//	"CREATION_DATE" DATE NOT NULL ENABLE, 
//	"DELETED_P" NUMBER, 
//	"QUERY_OPERATORS" VARCHAR2(4000 BYTE), 
//	"QUERY_VALUES" VARCHAR2(4000 BYTE), 
//	"LOGICAL_JOINS" VARCHAR2(4000 BYTE), 
//	 CONSTRAINT "ACS_RULES_CUST_ATTR_CRIT_PK" PRIMARY KEY ("RULES_CUST_ATTR_CRITERIA_ID")
//}
// 
// CREATE TABLE "APP"."ACS_RULES_CATEGORIES" 
// (	"RULE_CATEGORY_ID" NUMBER NOT NULL ENABLE, 
//	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"CATEGORY_ID" NUMBER NOT NULL ENABLE, 
//	 CONSTRAINT "ACS_RULES_CATEGORIES_PK" PRIMARY KEY ("RULE_CATEGORY_ID")
//}
// 
// CREATE TABLE "APP"."ACS_RULES_VIEWS" 
// (	"RULE_VIEW_ID" NUMBER NOT NULL ENABLE, 
//	"RULE_MASTER_ID" NUMBER NOT NULL ENABLE, 
//	"OWNER" NUMBER NOT NULL ENABLE, 
//	"CREATOR" NUMBER, 
//	"CREATION_DATE" DATE, 
//	"DELETED_P" NUMBER, 
//	"VIEW_NAME" VARCHAR2(32 BYTE) NOT NULL ENABLE, 
//}
 



public class DaoRule {
	private static Logger logger = LoggerFactory.getLogger(DaoRule.class);

	private static final String CREATE_RULE_TABLE_SQL = "CREATE TABLE rule (sid bigint, name varchar (128), description varchar(512), criteria varchar(1024), birthday timestamp, primary key(sid)";
	private static final String DROP_RULE_TABLE_SQL = "DROP TABLE rule";
	private static final String INSERT_RULE_SQL = "INSERT INTO rule (sid, name, description, criteria, birthday) VALUES (?, ?, ?, ?, ?)";
	private static final String UPDATE_RULE_SQL = "INSERT INTO rule (sid, name, description, criteria, birthday) VALUES (?, ?, ?, ?, ?)";

	private static final String CREATE_RULE2POLICY_TABLE_SQL = "CREATE TABLE rule2policy (sid bigint, rule_sid bigint, policy_sid bigint, version timestamp, primary key(sid)) ";
	private static final String DROP_RULE2POLICY_TABLE_SQL = "DROP TABLE rule2policy";
	private static final String INSERT_RULE2POLICY_SQL = "INSERT INTO rule2policy (sid, rule_sid, policy_sid, version) VALUES (?, ?, ?, ?)";

	// policy and rule has a M-to-M relationship and status is always add
	private static final String FIND_RULE_BY_POLICY_SID_SQL = "SELECT R.*, R2P.* FROM rule R join rule2policy R2P on R.sid=R2P.rule_sid WHERE R2P.policy_sid=?";

	private final static DaoRule s_singleton = new DaoRule();

	public static DaoRule getInstance() {
		return s_singleton;
	}

	private DaoRule() {
		// use singleton instead of generating a new object
	}

	private List<String> deserializedCriteriaList(String criteria) {
		List<String> cList = FoundryUtil.tokenize(criteria, FoundryConstants.DELIMITER_CRITERIA_LIST);
		return cList;
	}

	private String serializedCriteriaList(List<String> cList) {
		
		int len = 0;
		for (String c : cList) {
			len = len + c.length();
		}
		len += cList.size() -1;
		
		ByteBuffer buffer = ByteBuffer.allocate(len);
		
		
		for (int i=0; i< cList.size(); i++ ) {
			if ( i>0) {
			buffer.put((byte)FoundryConstants.DELIMITER_CRITERIA_LIST);
			}
			buffer.put(cList.get(i).getBytes());
		}
		
		return new String (buffer.array());
	}

	public VoRule createRule(VoRule rule, Long newPolicyId) throws SQLException {

		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		// 1. get unique number for mid and time stamp
		long sid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());

		// Timestamp ts = rs.getTimestamp(colnum); // column is TIMESTAMP
		// return ts !=null ? new Date(ts.getTime) : null;

		// 2. create master group
		// mid, type, birthday
		try {
			stmt = conn.prepareStatement(INSERT_RULE_SQL);
			stmt.setLong(1, sid);
			stmt.setString(2, rule.getName());
			stmt.setString(3, rule.getDescription());
			stmt.setString(4, serializedCriteriaList(rule.getCriteriaList()));
			stmt.setTimestamp(5, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert master policy : " + rule);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

		rule.setSid(sid);
		rule.setBirthday(now);
		return rule;

	}

	private VoRule buildRule(ResultSet rs) throws SQLException {
		VoRule rule = new VoRule();
		rule.setSid(rs.getLong("sid"));
		rule.setName(rs.getString("name"));
		rule.setDescription(rs.getString("description"));
		
//		String criteria = rs.getString("criteria");
		
		
		String criteria = rs.getString("criteria");
        List<String> cList = deserializedCriteriaList(criteria);
		
		rule.setCriteriaList(cList);
		Timestamp ts = rs.getTimestamp("birthday");
		rule.setBirthday(new Date(ts.getTime()));
		ts = rs.getTimestamp("version");
		rule.setVersion(new Date(ts.getTime()));

		return rule;
	}

	public List<VoRule> getRulesByPolicyId(Long policySid) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		try {
			List<VoRule> rList = new ArrayList<VoRule>();
			VoRule rule = null;
			stmt = conn.prepareStatement(FIND_RULE_BY_POLICY_SID_SQL);
			stmt.setLong(1, policySid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				rule = buildRule(rs);
				rList.add(rule);
			}
			return rList;

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	public void bindRuleByPolicy(long ruleSid, Long policySid) throws SQLException {
		DaoTransContext context = TransactionManager.getContext();
		Connection conn = context.getConnection();
		PreparedStatement stmt = null;

		long sid = DaoSequence.getInstance().getUniqueId();
		Timestamp now = new Timestamp(new Date().getTime());
		try {
			stmt = conn.prepareStatement(INSERT_RULE2POLICY_SQL);
			stmt.setLong(1, sid);
			stmt.setLong(2, ruleSid);
			stmt.setLong(3, policySid);
			stmt.setTimestamp(4, now);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to insert to rule2policy table policyId=" + policySid + ", ruleId=" + ruleSid);
			}
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

}
