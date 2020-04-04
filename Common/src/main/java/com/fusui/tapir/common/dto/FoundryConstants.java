
package com.fusui.tapir.common.dto;

public interface FoundryConstants {
	

	public static final byte DELIMITER_CRITERIA_OP = '\u0006';	// ascii back space
	public static final byte DELIMITER_CRITERIA_LIST = '\u0008';		// ascii acknowledge between each criteria line
	
	public static final int STATUS_CREATED = 0;
	public static final int STATUS_UPDATED = 1;
	public static final int STATUS_DELETED = -1;
	/***********************************************************************************
	 *
	 * Health Check
	 * 
	 ***********************************************************************************/
	public static final String CHECK_HEALTH_SUCCESS = "success\n";	

	/***********************************************************************************
	 *
	 * Restful Resources
	 * 
	 ***********************************************************************************/

	public static final String RESOURCE_ROOT = "";
	public static final String RESOURCE_SYSTEM_HEALTH = "SYSTEM/health";
	public static final String RESOURCE_VERSION = "version";
	public static final String RESOURCE_PROPERTIES = "properties";
		
		
	public static final String PATH_PARAM_USER_ID = "userId";
	public static final String PATH_PARAM_OBJECT_ID = "objId";
	public static final String PATH_PARAM_OBJECT_TYPE = "objType";
	public static final String PATH_PARAM_ACTION = "action";

	
	/***********************************************************************************
	 *
	 * Restful Service Error Code
	 * 
	 ***********************************************************************************/
	
	public static final int ERROR_CODE_SYSTEM = 1000;
	public static final int ERROR_CODE_APPLICATION = 2000;

	public static final String RESOURCE_FOUNDRY = "foundry";
	
	
	
	
	/***********************************************************************************
	 *
	 * Policy and Rule evaluation
	 * 
	 ***********************************************************************************/
	// **********************************************************************
	// Role
	// **********************************************************************

	public static final String SYSTEM_ROLE_ADMIN = "$Admin";
	public static final String SYSTEM_ROLE_OWNER = "$Owner";

	// **********************************************************************
	// Policy
	// **********************************************************************
	
	public static final String VALUE_DELIMITER = ",";

	public static final String POLICY_ACTION_DELETE = "Delete";
	public static final String POLICY_ACTION_MODIFY = "Modify";
	public static final String POLICY_ACTION_READ   = "Read";
	public static final String POLICY_ACTION_UPLOAD = "Upload";
	//public static final String POLICY_ACTION_ALL = POLICY_ACTION_UPLOAD+"|"+POLICY_ACTION_DELETE+"|"+POLICY_ACTION_READ;
	public static final String POLICY_ACTION_DMR = POLICY_ACTION_READ+VALUE_DELIMITER+POLICY_ACTION_MODIFY+VALUE_DELIMITER+POLICY_ACTION_DELETE;
	public static final String POLICY_ACTION_ALL = POLICY_ACTION_READ+VALUE_DELIMITER+POLICY_ACTION_MODIFY+VALUE_DELIMITER+POLICY_ACTION_DELETE+VALUE_DELIMITER+POLICY_ACTION_UPLOAD;
		
	public static final String POLICY_RCA_DENY_OVERRIDE = "DenyOverride";
	public static final String POLICY_RCA_PERMIT_OVERRIDE = "PermitOverride";
	public static final String POLICY_RCA_DEFAULT = POLICY_RCA_DENY_OVERRIDE;
	
	// TODO
	public static final String POLICY_ATTRIBUTE_POLICY_ACTIONS = "$Ply.PolicyActions";
	
	
	// **********************************************************************
	// Rule
	// **********************************************************************
	
	public static final String RULE_VALUE_TYPE_STRING 	= "String";
	public static final String RULE_VALUE_TYPE_INTEGER 	= "Integer";
	public static final String RULE_VALUE_TYPE_LONG	 	= "Long";
	public static final String RULE_VALUE_TYPE_DATE 	= "Date";
	public static final String RULE_VALUE_TYPE_BOOLEAN 	= "Boolean";
	
	public static final String RULE_OPERATOR_AND = "And";
	public static final String RULE_OPERATOR_OR = "Or";
	public static final String RULE_OPERATOR_NOT = "Not";
	
	public static final String RULE_NODE_ROOT_ID = "0";
	public static final String RULE_NODE_TYPE_OPERAND = "Operand";
	public static final String RULE_NODE_TYPE_OPERATOR = "Operator";
	
	
	public static final String RULE_NODE_VARIABLE_USER = "$User.";
	public static final String RULE_NODE_VARIABLE_APP = "$App.";
	public static final String RULE_NODE_VARIABLE_MACHINE = "$Mac.";
	public static final String RULE_NODE_VARIABLE_ENV = "$Env.";
	
	public static final int RULE_NODE_VARIABLE_USER_LENGTH = RULE_NODE_VARIABLE_USER.length();
	public static final int RULE_NODE_VARIABLE_APP_LENGTH = RULE_NODE_VARIABLE_APP.length();
	public static final int RULE_NODE_VARIABLE_MACHINE_LENGTH = RULE_NODE_VARIABLE_MACHINE.length();
	
	public static final int RULE_NODE_VARIABLE_ENV_LENGTH = RULE_NODE_VARIABLE_ENV.length();
	
//	public static final int RULE_UI_STATUS_NONE = 0;
//	public static final int RULE_UI_STATUS_ADD = 1;
//	public static final int RULE_UI_STATUS_REMOVE = 2;
//	public static final int RULE_UI_STATUS_ADD_NEW = 3;
//	public static final int RULE_UI_STATUS_MODIFY = 4;
	
	public static final String ATTRIBUTE_UI_TYPE_TEXT_FIELD 		= "TF";
	public static final String ATTRIBUTE_UI_TYPE_SINGLE_SELECTION 	= "SS";
	public static final String ATTRIBUTE_UI_TYPE_MULTIPLE_SELECTION = "MS";
	public static final String ATTRIBUTE_UI_TYPE_DATE				= "DD";
	public static final String ATTRIBUTE_UI_TYPE_TIME				= "TT";
	public static final String ATTRIBUTE_UI_TYPE_DATETIME			= "DT";
	
	public static final String ATTRIBUTE_UI_TYPE_BOOLEAN			= "BL";
	
	public static final String DATA_TYPE_STRING = "String";
	public static final String DATA_TYPE_LONG = "Long";
	public static final String DATA_TYPE_INTEGER = "Integer";
	public static final String DATA_TYPE_BOOLEAN = "Boolean";
	public static final String DATA_TYPE_DATETIME = "Datetime";
	public static final String DATA_TYPE_DOUBLE = "Double";
	public static final String DATA_TYPE_LOGICAL_OPERATOR = "LogicalOperator";
	
	// **********************************************************************
	// Attribute Env
	// **********************************************************************
	
	public static final String ATTRIBUTE_ENV_WEEKDAY_MONDAY 	= "Monday";
	public static final String ATTRIBUTE_ENV_WEEKDAY_TUESDAY 	= "Tuesday";
	public static final String ATTRIBUTE_ENV_WEEKDAY_WEDNESDAY 	= "Wednesday";
	public static final String ATTRIBUTE_ENV_WEEKDAY_THURSDAY 	= "Thursday";
	public static final String ATTRIBUTE_ENV_WEEKDAY_FRIDAY 	= "Friday";
	public static final String ATTRIBUTE_ENV_WEEKDAY_SATURDAY 	= "Saturday";
	public static final String ATTRIBUTE_ENV_WEEKDAY_SUNDAY 	= "Sunday";
		
	
}
