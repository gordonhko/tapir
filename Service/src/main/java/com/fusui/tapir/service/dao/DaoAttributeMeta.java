package com.fusui.tapir.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.VoMetaOperand;
import com.fusui.tapir.common.dto.VoMetaOperator;
import com.fusui.tapir.service.dal.TransactionManager;

public class DaoAttributeMeta {

	// PK is attrName
	// attr_name -> unique name used in app
	// attr_ui_name -> UI display
	// attr_db_name -> DB column name
	private static final String CREATE_ATTR_META_TABLE_SQL = "CREATE TABLE meta_operand (attr_name varchar(64), attr_ui_name varchar(64), attr_db_name varchar(64), attr_default_value varchar(1024), attr_data_type varchar(32), attr_selection_type varchar(8), primary key(attr_name)) ";
	private static final String DROP_ATTR_META_TABLE_SQL = "DROP TABLE meta_operand";
	private static final String INSERT_ATTR_META_SQL = "INSERT INTO meta_operand (attr_name, attr_ui_name, attr_value, attr_data_type, attr_selection_type, attr_db_name) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String DELETE_ATTR_META_SQL = "DELETE from meta_operand WHERE attr_name=?";
	private static final String UPDATE_ATTR_META_SQL = "UPDATE meta_operand SET attr_ui_name=?, attr_value=?, attr_selection_type=? WHERE attr_name=?";
	private static final String FIND_NUMBER_OF_ATTR_META_SQL = "SELECT COUNT(*) FROM meta_operand";
	private static final String FIND_ATTR_META_BY_ID_SQL = "SELECT * FROM meta_operand WHERE attr_name = ?";
	private static final String FIND_ATTR_META_BY_VALUE_AND_SELECTION_TYPES_SQL = "SELECT * FROM meta_operand WHERE attr_data_type = ? AND attr_selection_type = ?";
	private static final String FIND_ALL_ATTR_META_SQL = "SELECT * FROM meta_operand ORDER BY attr_name";
	private static final String FIND_ALL_POLICY_ATTR_META_SQL = "SELECT * FROM meta_operand WHERE attr_name LIKE '$Ply.%' ORDER BY attr_name";

	// PK is operator_name+data_type+selection_type
	// operator_name -> UI display as well as used in system
	// operator_method_name (and operator_class_name) -> evaluation fx used by app
	// operator_db_name -> DB where clause operator name

	private static final String CREATE_OPER_META_TABLE_SQL = "CREATE TABLE meta_operator (operator_name varchar(512), data_type varchar(64), selection_type varchar(8),  operator_class_name varchar(512), operator_method_name varchar(64), operand_number integer, operator_ui_name varchar(32), operator_db_name varchar(64), status smallint, birthday timestamp, primary key(operator_name, data_type, selection_type)";
	private static final String DROP_OPER_META_TABLE_SQL = "DROP TABLE meta_operator";
	
	private static final String INSERT_OPER_META_SQL = "INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) VALUES (?, ?, ?, ?, ?, ?, ?, 0, ?)";
	private static final String DELETE_OPER_META_SQL = "INSERT INTO meta_operator (operator_name, data_type, selection_type, operator_class_name, operator_method_name, operand_number, operator_ui_name, operator_db_name, status, birthday) VALUES (?, ?, ?, ?, ?, ?, ?, -1, ?)";

	
//	private static final String UPDATE_OPER_META_SQL = "UPDATE meta_operator SET operator_class_name=?, operator_method_name=?, operator_criteria_enable=? WHERE attr_data_type=? AND attr_selection_type=? AND operator_name=?";
	private static final String FIND_NUMBER_OF_OPER_META_SQL = "SELECT COUNT(*) FROM meta_operator";
	private static final String FIND_ALL_OPER_META_SQL = "SELECT * FROM meta_operator ORDER BY data_type, selection_type";
	private static final String DELETE_OPER_META_BY_ATTR_VALUE_TYPE_SQL = "DELETE from meta_operator WHERE data_type=?";

	private static Logger logger = LoggerFactory.getLogger(DaoAttributeMeta.class);

	private final static DaoAttributeMeta s_singleton = new DaoAttributeMeta();

	public static DaoAttributeMeta getInstance() {
		return s_singleton;
	}

	private DaoAttributeMeta() {
		// use singleton instead of generating a new object
	}

	// **********************************************************************
	// DML
	// **********************************************************************

	// **********************************************************************
	// Get Attributes
	// **********************************************************************

	private VoMetaOperand buildAttribute(ResultSet rs) throws SQLException {
		VoMetaOperand attribute = new VoMetaOperand();
		attribute.setAttrName(rs.getString("attr_name"));
		attribute.setAttrUIName(rs.getString("attr_ui_name"));
		attribute.setPossibleValues(rs.getString("attr_default_value"));
		attribute.setValueType(rs.getString("attr_data_type"));
		attribute.setSelectionType(rs.getString("attr_selection_type"));
		attribute.setAttrDBName(rs.getString("attr_db_name"));
		return attribute;
	}

	public VoMetaOperand findAttributeById(String attrName) throws SQLException {

		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.prepareStatement(FIND_ATTR_META_BY_ID_SQL);
			stmt.setString(1, attrName);
			rs = stmt.executeQuery();
			if (rs.next()) {
				VoMetaOperand attribute = buildAttribute(rs);
				return attribute;
			}

			throw new SQLException("attrName cannot be found:" + attrName);

		} catch (SQLException e) {
			logger.error("Unable to get userrole from userrole table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public List<VoMetaOperand> findAttributeByValueAndSelectionTypes(String valueType, String selectionType) throws SQLException {

		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<VoMetaOperand> result = new ArrayList<VoMetaOperand>();
		try {
			stmt = conn.prepareStatement(FIND_ATTR_META_BY_VALUE_AND_SELECTION_TYPES_SQL);
			stmt.setString(1, valueType);
			stmt.setString(2, selectionType);
			rs = stmt.executeQuery();
			while (rs.next()) {
				VoMetaOperand attribute = buildAttribute(rs);
				result.add(attribute);
			}

			return result;

		} catch (SQLException e) {
			logger.error("attribute cannot be found: valueType=" + valueType + " selectionType=" + selectionType + " error : " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public int findNumberOfAttributes() throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(FIND_NUMBER_OF_ATTR_META_SQL);
			rs.next();
			int count = rs.getInt("count");
			return count;
		} catch (SQLException e) {
			logger.info("DaoUser.countByUserId: Unable to query attribute table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public List<VoMetaOperand> findAttributes() throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			List<VoMetaOperand> attributeList = new ArrayList<VoMetaOperand>();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(FIND_ALL_ATTR_META_SQL);
			while (rs.next()) {
				VoMetaOperand attribute = buildAttribute(rs);
				attributeList.add(attribute);
			}

			return attributeList;
		} catch (SQLException e) {
			logger.info("DaoUser.findAll: Unable to query all attribute table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public List<VoMetaOperand> findPolicyAttributes() throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			List<VoMetaOperand> attributeList = new ArrayList<VoMetaOperand>();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(FIND_ALL_POLICY_ATTR_META_SQL);
			while (rs.next()) {
				VoMetaOperand attribute = buildAttribute(rs);
				attributeList.add(attribute);
			}

			return attributeList;
		} catch (SQLException e) {
			logger.info("DaoUser.findAll: Unable to query all attribute table for policy: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	// **********************************************************************
	// Insert, Update and Delete Attribute Metadata
	// **********************************************************************

	public VoMetaOperand addAttribute(VoMetaOperand attribute) throws SQLException {
		if (FoundryUtil.isEmpty(attribute.getAttrName()) || FoundryUtil.isEmpty(attribute.getAttrUIName()) || FoundryUtil.isEmpty(attribute.getValueType())
				|| FoundryUtil.isEmpty(attribute.getAttrDBName())
		/* || FoundryUtil.isEmpty(attribute.getSelectionType()) */) {
			throw new SQLException("input parameter cannot be empty");
		}

		// String possibleValue = null;
		// List <String> values = attribute.getPossibleValues();
		// if (null !=values && values.size() > 0) {
		// StringBuilder sb = new StringBuilder();
		// for (String value : values) {
		// if (sb.length() > 0) {
		// sb.append(VoPolicyConst.VALUE_DELIMITER);
		// }
		// sb.append(value);
		// }
		// possibleValue = sb.toString();
		// }

		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_ATTR_META_SQL);
			stmt.setString(1, attribute.getAttrName());
			stmt.setString(2, attribute.getAttrUIName());
			// stmt.setString(3, possibleValue);
			stmt.setString(3, attribute.getPossibleValues());
			stmt.setString(4, attribute.getValueType());
			stmt.setString(5, attribute.getSelectionType());
			stmt.setString(6, attribute.getAttrDBName());
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				// TODO, check PK duplicated
				throw new SQLException("Unable to insert attribute since attributeId mismatched : " + attribute.getAttrName());
			}

			return attribute;

		} catch (SQLException e) {
			logger.error("Unable to insert attribute into attribute table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public VoMetaOperand updateAttribute(String possibleValues, String selectionType) throws SQLException {
		VoMetaOperand attribute = new VoMetaOperand();
		attribute.setPossibleValues(possibleValues);
		attribute.setSelectionType(selectionType);
		return updateAttribute(attribute);
	}

	public VoMetaOperand updateAttribute(VoMetaOperand attribute) throws SQLException {

		if (FoundryUtil.isEmpty(attribute.getAttrName()) || FoundryUtil.isEmpty(attribute.getAttrUIName()) || FoundryUtil.isEmpty(attribute.getValueType())
				|| FoundryUtil.isEmpty(attribute.getAttrDBName())
		/* || FoundryUtil.isEmpty(attribute.getSelectionType()) */) {
			throw new SQLException("input parameter cannot be empty");
		}

		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		try {

			// String possibleValue = null;
			// List <String> values = attribute.getPossibleValues();
			// if (null !=values && values.size() > 0) {
			// StringBuilder sb = new StringBuilder();
			// for (String value : values) {
			// if (sb.length() > 0) {
			// sb.append(VoPolicyConst.VALUE_DELIMITER);
			// }
			// sb.append(value);
			// }
			// possibleValue = sb.toString();
			// }

			stmt = conn.prepareStatement(UPDATE_ATTR_META_SQL);

			stmt.setString(1, attribute.getAttrUIName());
			stmt.setString(2, attribute.getPossibleValues());
			// stmt.setString(2, possibleValue);
			stmt.setString(3, attribute.getSelectionType());
			stmt.setString(4, attribute.getAttrName());
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				throw new SQLException("Unable to update attribute since attributeId mismatched : " + attribute.getAttrName());
			}
			return attribute;

		} catch (SQLException e) {
			logger.info("Unable to update attribute table: " + attribute.getAttrName() + " msg=" + e.getMessage());
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public void deleteAttribute(String attrName) throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_ATTR_META_SQL);
			stmt.setString(1, attrName);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				logger.warn("Unable to delete attribute since attributeId mismatched : " + attrName);
			}

		} catch (SQLException e) {
			logger.error("Unable to delete attribute from attribute table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	// **********************************************************************
	// Operator
	// **********************************************************************

	private VoMetaOperator buildOperator(ResultSet rs) throws SQLException {
		VoMetaOperator operator = new VoMetaOperator();
		operator.setValueType(rs.getString("data_type"));
		operator.setSelectionType(rs.getString("selection_type"));
		operator.setOperatorName(rs.getString("operator_name"));
		operator.setNumOfOperands(rs.getInt("operand_number"));
		operator.setClassName(rs.getString("operator_class_name"));
		operator.setMethodName(rs.getString("operator_method_name"));
		operator.setOperatorUIName(rs.getString("operator_ui_name"));
		operator.setOperatorDBName(rs.getString("operator_db_name"));
		//operator.setCriteriaEnable(rs.getString("operator_criteria_enable"));
		return operator;
	}

	public int findNumberOfOperators() throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(FIND_NUMBER_OF_OPER_META_SQL);
			rs.next();
			int count = rs.getInt("count");
			return count;
		} catch (SQLException e) {
			logger.info("DaoUser.countByUserId: Unable to query attribute table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public List<VoMetaOperator> findOperators() throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			List<VoMetaOperator> opList = new ArrayList<VoMetaOperator>();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(FIND_ALL_OPER_META_SQL);
			while (rs.next()) {
				VoMetaOperator attribute = buildOperator(rs);
				opList.add(attribute);
			}

			return opList;
		} catch (SQLException e) {
			logger.info("DaoUser.findAll: Unable to query all attribute table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	// **********************************************************************
	// Insert, Update and Delete Operator
	// **********************************************************************

	public VoMetaOperator addOperator(VoMetaOperator operator) throws SQLException {
		if (FoundryUtil.isEmpty(operator.getValueType()) || FoundryUtil.isEmpty(operator.getSelectionType()) || FoundryUtil.isEmpty(operator.getOperatorName())
				|| FoundryUtil.isEmpty(operator.getCriteriaEnable()) || FoundryUtil.isEmpty(operator.getClassName()) || FoundryUtil.isEmpty(operator.getMethodName())
				|| FoundryUtil.isEmpty(operator.getOperatorDBName()) || /* FoundryUtil.isEmpty(operator.getOperatorUIName()) || */
				(operator.getNumOfOperands() <= 0)) {
			throw new SQLException("input parameter cannot be empty");
		}

		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(INSERT_OPER_META_SQL);
			stmt.setString(1, operator.getValueType());
			stmt.setString(2, operator.getSelectionType());
			stmt.setString(3, operator.getOperatorName());
			stmt.setString(4, operator.getClassName());
			stmt.setString(5, operator.getMethodName());
			stmt.setInt(6, operator.getNumOfOperands());
			stmt.setString(7, operator.getOperatorUIName());
			stmt.setString(8, operator.getOperatorDBName());
			stmt.setString(9, operator.getCriteriaEnable());
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				// TODO, check PK duplicated
				throw new SQLException("Unable to insert operator since operatorId mismatched : " + operator.getValueType());
			}

			return operator;

		} catch (SQLException e) {
			logger.error("Unable to insert operator into operator table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

//	public VoOperatorMeta updateOperator(int nOperand) throws SQLException {
//		VoOperatorMeta operator = new VoOperatorMeta();
//		return updateOperator(operator);
//	}
//
//	public VoOperatorMeta updateOperator(VoOperatorMeta operator) throws SQLException {
//
//		if (FoundryUtil.isEmpty(operator.getValueType()) || FoundryUtil.isEmpty(operator.getSelectionType()) || FoundryUtil.isEmpty(operator.getOperatorName())
//				|| FoundryUtil.isEmpty(operator.getCriteriaEnable()) || FoundryUtil.isEmpty(operator.getClassName()) || FoundryUtil.isEmpty(operator.getMethodName())
//				|| FoundryUtil.isEmpty(operator.getOperatorDBName()) || /* FoundryUtil.isEmpty(operator.getOperatorUIName()) || */
//				(operator.getNumOfOperands() <= 0)) {
//			throw new SQLException("input parameter cannot be empty");
//		}
//
//		Connection conn = TransactionManager.getContext().getConnection();
//		PreparedStatement stmt = null;
//		try {
//
//			stmt = conn.prepareStatement(UPDATE_OPER_META_SQL);
//
//			stmt.setString(1, operator.getClassName());
//			stmt.setString(2, operator.getMethodName());
//			stmt.setString(3, operator.getCriteriaEnable());
//			stmt.setString(4, operator.getValueType());
//			stmt.setString(5, operator.getSelectionType());
//			stmt.setString(6, operator.getOperatorName());
//			int nRows = stmt.executeUpdate();
//			if (nRows == 0) {
//				throw new SQLException("Unable to update operator since operatorId mismatched : " + operator.getValueType());
//			}
//			return operator;
//
//		} catch (SQLException e) {
//			logger.info("Unable to update operator table: " + operator.getValueType() + " msg=" + e.getMessage());
//			throw e;
//		} finally {
//			try {
//				if (stmt != null) {
//					stmt.close();
//				}
//				// if (conn != null) {
//				// DaoTransManager.closeConnection(conn)(conn);
//				// }
//			} catch (SQLException e1) {
//				logger.error("Unable to close statement or connection: " + e1.getMessage());
//				throw e1;
//			}
//		}
//	}

	public void deleteOperator(String attrName, String selectionType, String operatorName) throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_OPER_META_SQL);
			stmt.setString(1, attrName);
			stmt.setString(2, selectionType);
			stmt.setString(3, operatorName);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				logger.warn("Unable to delete operator from operator table with attr=" + attrName + "oper=" + operatorName);
			}

		} catch (SQLException e) {
			logger.error("Unable to delete operator from operator table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}

	public void deleteOperatorsByAttribute(String attrName) throws SQLException {
		Connection conn = TransactionManager.getContext().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(DELETE_OPER_META_BY_ATTR_VALUE_TYPE_SQL);
			stmt.setString(1, attrName);
			int nRows = stmt.executeUpdate();
			if (nRows == 0) {
				logger.warn("Unable to delete operator from operator table : " + attrName);
			}

		} catch (SQLException e) {
			logger.error("Unable to delete operator from operator table: " + e.getMessage());
			throw e;
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				// if (conn != null) {
				// DaoTransManager.closeConnection(conn)(conn);
				// }
			} catch (SQLException e1) {
				logger.error("Unable to close statement or connection: " + e1.getMessage());
				throw e1;
			}
		}
	}
}
