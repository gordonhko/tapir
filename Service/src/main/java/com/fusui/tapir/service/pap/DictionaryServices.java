package com.fusui.tapir.service.pap;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoDictionary;
import com.fusui.tapir.common.dto.VoMetaOperand;
import com.fusui.tapir.common.dto.VoMetaOperator;
import com.fusui.tapir.common.dto.VoMetaOperator.VoOperatorMetaList;
import com.fusui.tapir.service.dao.DaoAttributeMeta;
import com.fusui.tapir.service.dao.DaoUser;

public class DictionaryServices {

	private static Logger logger = LoggerFactory.getLogger(DictionaryServices.class);
	
	private static final String[][] ATTR_NAME_METHOD_ARRAY = {
		
		
		{"$User.UserStatus", "getAllUserStatus"},

		{"$Ply.PolicyType", "getAllPolicyTypeNames"},
		{"$Ply.PolicyActions", "getAllPolicyActionNames"},
		{"$Ply.PolicyRca", "getAllPolicyRca"},
	
		{"$Env.CurrentWeekday", "getAllWeekdays"}
	};
	
	private static Map <String, Method> methodMap = new HashMap <String, Method>();
	private final DaoAttributeMeta attrDao = DaoAttributeMeta.getInstance();
	
	private static DictionaryServices s_singleton = new DictionaryServices();
	
	public static DictionaryServices getInstance() {
		return s_singleton;
	}

	private DictionaryServices() {
		try {
			initPossibleValuesMap();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	// **********************************************************************
	// Get all attributes
	// **********************************************************************
	
	public VoDictionary getDictionary() throws SQLException  {
		
		List<VoMetaOperand> attrList =  attrDao.findAttributes();
		List<VoMetaOperator> operList =  attrDao.findOperators();
		
		int maxOperatorLen = 0;
		
		// Build a map, key is valueType (String.class, Boolean.class ...) + selectionType, value is a list of VoOperatorMeta
		HashMap <String,  VoOperatorMetaList> opMap = new HashMap <String, VoOperatorMetaList>();
		for (VoMetaOperator operator : operList) {
			
			String key = FoundryUtil.createOperatorKeyByVS(operator);
			VoOperatorMetaList list = opMap.get(key);
			if (null == list) {
				list = new VoOperatorMetaList ();
				List <VoMetaOperator> opList = new ArrayList <VoMetaOperator>();
				list.setList(opList);
				opMap.put(key, list);
			}
			list.getList().add(operator);
			
			int opLen = operator.getOperatorName().length();
			if (opLen > maxOperatorLen ) {
				maxOperatorLen = opLen;
			}
		}
		
		// now for each attribute, setup reference at "setOpListName' and point to VoOperatorMetaList at hash map
		List <VoMetaOperand> validAttrList = new ArrayList <VoMetaOperand>();
		for (VoMetaOperand attribute : attrList) {
			
			attribute.setPossibleValues(getPossibleValues(attribute));
			
			// try attribute name first to see if the op list can be found
			String key = FoundryUtil.createOperatorKeyByVS(attribute);
			if ( null != opMap.get(key) ) {
				attribute.setOperKey(key);
				validAttrList.add(attribute);
				continue;
			}

//			if ( null != opMap.get(attribute.getValueType()) ) {
//				attribute.setOperKey(attribute.getValueType());
//				validAttrList.add(attribute);
//				continue;
//			}
			
			// this is bad, it should not happen
			logger.error("Unable to create attribute: " + attribute.getAttrName());
			System.out.println("Unable to create attribute: " + attribute.getAttrName());
			new Exception().printStackTrace();
		}
		
		VoDictionary dict = new VoDictionary (validAttrList, opMap, maxOperatorLen);
		return dict;
	}

	

	// **********************************************************************
	// Create, Update and Delete Attribute
	// **********************************************************************
	
	public VoMetaOperand createAttributeMeta(VoMetaOperand attribute) throws SQLException, TapirException {
		VoMetaOperand newAttr = attrDao.addAttribute(attribute);
		return newAttr;
	}
	
	public VoMetaOperand updateAttributeMeta(VoMetaOperand attribute) throws SQLException, TapirException {
		VoMetaOperand newAttr = attrDao.updateAttribute(attribute);
		return newAttr;
	}

	public void deleteAttributeMeta(String attrName) throws SQLException, TapirException {
		attrDao.deleteOperatorsByAttribute(attrName);
		attrDao.deleteAttribute(attrName);
	}
	
	// **********************************************************************
	// Create, Update and Delete Operator
	// **********************************************************************
	
	public VoMetaOperator createOperatorMeta(VoMetaOperator Operator) throws SQLException, TapirException {
		VoMetaOperator newAttr = attrDao.addOperator(Operator);
		return newAttr;
	}
	
//	public VoOperatorMeta updateOperatorMeta(VoOperatorMeta Operator) throws SQLException, FoundryException {
//		VoOperatorMeta newOp = attrDao.updateOperator(Operator);
//		return newOp;
//	}

	public void deleteOperatorMeta(String valueType, String selectionType, String operatorName) throws SQLException, TapirException {
		List <VoMetaOperand> attrList = attrDao.findAttributeByValueAndSelectionTypes (valueType, selectionType);
		if (null != attrList && attrList.size() > 0  ) {
			throw new SQLException("Cannot delete opreator =" + operatorName + " since it is used with attribute name=" + attrList.get(0));
		}
		attrDao.deleteOperator(valueType, selectionType, operatorName);
	}	
	
	public void deleteOperatorMetaByAttribute(String attrName) throws SQLException, TapirException {
		if (null != attrDao.findAttributeById (attrName) ) {
			throw new SQLException("Cannot delete opreator since it is used with attribute name=" + attrName);
		}
		attrDao.deleteOperatorsByAttribute(attrName);
	}
	
	// **********************************************************************
	// Possible Values Getter
	// **********************************************************************

	public String getAllUserStatus() {
		return null;//DaoUser.USER_STATUS_ACTIVE + FoundryConstants.VALUE_DELIMITER + DaoUser.USER_STATUS_INACTIVE;
	}

	public String getAllPolicyActionNames() {
		return FoundryConstants.POLICY_ACTION_READ + FoundryConstants.VALUE_DELIMITER + FoundryConstants.POLICY_ACTION_MODIFY + FoundryConstants.VALUE_DELIMITER + FoundryConstants.POLICY_ACTION_DELETE + FoundryConstants.VALUE_DELIMITER + FoundryConstants.POLICY_ACTION_UPLOAD;
	}

	public String getAllPolicyRca() {
		return FoundryConstants.POLICY_RCA_DENY_OVERRIDE + FoundryConstants.VALUE_DELIMITER + FoundryConstants.POLICY_RCA_PERMIT_OVERRIDE;
	}

	public String getAllPolicyTypeNames() {
		// not defined yet
		return "";
	}

	public String getAllWeekdays() {
		return 	FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_MONDAY + FoundryConstants.VALUE_DELIMITER + 
				FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_TUESDAY + FoundryConstants.VALUE_DELIMITER +
				FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_WEDNESDAY + FoundryConstants.VALUE_DELIMITER +
				FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_THURSDAY + FoundryConstants.VALUE_DELIMITER +
				FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_FRIDAY + FoundryConstants.VALUE_DELIMITER +
				FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_SATURDAY + FoundryConstants.VALUE_DELIMITER +
				FoundryConstants.ATTRIBUTE_ENV_WEEKDAY_SUNDAY;
	}
	
	private void initPossibleValuesMap() throws NoSuchMethodException {
		 for (String[] entry : ATTR_NAME_METHOD_ARRAY ) {
			Method method = this.getClass().getMethod(entry[1]);
			methodMap.put(entry[0], method);
		 }
	}
	
	public String getPossibleValues(VoMetaOperand attribute) {
		try {
			Method method = methodMap.get(attribute.getAttrName());
			if (method != null) {
				return (String) method.invoke(this);
			}
			return null;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
