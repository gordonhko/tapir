package com.fusui.tapir.service.pap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.dto.VoApp;
import com.fusui.tapir.common.dto.VoDictionary;
import com.fusui.tapir.common.dto.VoGroup;
import com.fusui.tapir.common.dto.VoMachine;
import com.fusui.tapir.common.dto.VoMetaOperand;
import com.fusui.tapir.common.dto.VoMetaOperator;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.common.dto.VoUser;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dal.TransactionManager.DaoTransContext;
import com.fusui.tapir.service.dao.DaoGroup;
import com.fusui.tapir.service.dao.DaoMember;
import com.fusui.tapir.service.dao.DaoPolicy;
import com.fusui.tapir.service.dao.DaoRule;
import com.fusui.tapir.service.dao.DaoUser;

/*
 * @author gko
 */

public class PAPService {

	private static Logger logger = LoggerFactory.getLogger(PAPService.class);

	private final DictionaryServices s_dictionaryServices = DictionaryServices.getInstance();

	private final DaoGroup groupDao = DaoGroup.getInstance();
	private final DaoUser userDao = DaoUser.getInstance();
	private final DaoMember memberDao = DaoMember.getInstance();
	private final DaoPolicy policyDao = DaoPolicy.getInstance();
	private final DaoRule ruleDao = DaoRule.getInstance();

	private static PAPService instance = new PAPService();

	public static PAPService getInstance() {
		return instance;
	}

	private PAPService() {
	 
	}

	public VoDictionary getDictionary() throws TapirException {
		try {
			TransactionManager.transactionStart();
			VoDictionary dict = s_dictionaryServices.getDictionary();
			TransactionManager.transactionCommit();
			return dict;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	// *********************************************************************
	// U S E R
	// *********************************************************************

	//
	// Get User in General
	//
	public VoUser getUserById(long userId) throws TapirException {
		try {
			TransactionManager.transactionStart();
			VoUser newApp = null; //userDao.getUserByMid(userId);
			TransactionManager.transactionCommit();
			return newApp;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	public List<VoUser> getAllUsers() throws TapirException {
		return null;
	}

	//
	// Create, Update and Delete User
	//
	public VoUser createUser(VoUser user) throws SQLException, TapirException {
		try {
			TransactionManager.transactionStart();
			VoUser newUser = null; //userDao.createUser(user);
			TransactionManager.transactionCommit();
			return newUser;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	public VoUser updateUser(VoUser user) throws SQLException, TapirException {
		return null;
	}

	public void deleteUser(long userId) throws SQLException, TapirException {
	}

	public VoUser getUserByLoginName(String loginName) throws TapirException {
		try {
			TransactionManager.transactionStart();
			VoUser newUser = null; // userDao.getUserByLoginName(loginName);
			TransactionManager.transactionCommit();
			return newUser;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	//
	// User and Group Relationship
	//
	public List<VoUser> getUsersByGroup(long groupId) throws TapirException {
		return null;
	}

	public void bindUserByGroup(long userId, long groupId) throws TapirException {
		try {
			TransactionManager.transactionStart();
			//userDao.bindUserByGroup(userId, groupId);
			TransactionManager.transactionCommit();
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	public void unbindUserByGroup(long userId, long groupId) throws TapirException {
	}

	//
	// User and Policy Relationship
	//

	public List<VoUser> getUsersByPolicy(long policyId) throws TapirException {
		return null;
	}

	public void bindUserByPolicy(long userId, long policyId) throws TapirException {
		try {
			TransactionManager.transactionStart();
			//userDao.bindUserByPolicy(userId, policyId);
			TransactionManager.transactionCommit();
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}

	}

	public void unbindUserByPolicy(long userId, long policyId) throws TapirException {
	}

	// *********************************************************************
	// G R O U P
	// *********************************************************************

	//
	// Get Group in General
	//

	public VoGroup getGroupById(long groupId) throws TapirException {
		return null;
	}

	public List<VoGroup> getAllGroups() throws TapirException {
		return null;
	}

	// type can be user-group, machine-group and app-group
	public List<VoGroup> getGroupsByType(String type) throws TapirException {
		return null;
	}

	//
	// Create, Update and Delete Group
	//
	public VoGroup createGroup(VoGroup group) throws SQLException, TapirException {
		try {
			TransactionManager.transactionStart();
			//groupDao.createGroup(0L, 0L, group);
			TransactionManager.transactionCommit();
			return group;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	public VoGroup updateGroup(VoGroup group) throws SQLException, TapirException {
		return null;
	}

	public void deleteGroup(long groupId) throws SQLException, TapirException {
	}

	//
	// Group and User Relationship
	//

	public List<VoGroup> getGroupsByUser(long userId) throws TapirException {
		return null;
	}

	//
	// Group and Policy Relationship
	//

	public List<VoUser> getGroupsByPolicy(long policyId) throws TapirException {
		return null;
	}

	public void bindGroupByPolicy(long groupId, long policyId) throws TapirException {
	}

	public void unbindGroupByPolicy(long groupId, long policyId) throws TapirException {
	}

	// *********************************************************************
	// M A C H I N E
	// *********************************************************************
	public VoMachine getMachineById(long machineId) throws TapirException {
		return null;
	}

	public List<VoMachine> getAllMachines() throws TapirException {
		return null;
	}

	//
	// Create, Update and Delete Machine
	//
//	public VoMachine createMachine(VoMachine machine) throws SQLException, TapirException {
//		try {
//			TransactionManager.transactionStart();
//			VoMachine newMachine = machineDao.createMachine(machine);
//			TransactionManager.transactionCommit();
//			return newMachine;
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
//	}
//
//	public VoMachine updateMachine(VoMachine machine) throws SQLException, TapirException {
//		return null;
//	}
//
//	public void deleteMachine(long machineId) throws SQLException, TapirException {
//	}
//
//	//
//	// Machine and Group Relationship
//	//
//	public List<VoMachine> getMachinesByGroup(long groupId) throws TapirException {
//		return null;
//	}
//
//	public void bindMachineByGroup(long machineId, long groupId) throws TapirException {
//		try {
//			TransactionManager.transactionStart();
//			machineDao.bindMachineByGroup(machineId, groupId);
//			TransactionManager.transactionCommit();
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
//	}
//
//	public void unbindMachineByGroup(long machineId, long groupId) throws TapirException {
//	}

	//
	// Machine and Policy Relationship
	//

	// public List<VoMachine> getMachinesByPolicy(long policyId) throws FoundryException {
	// return null;
	// }
	//
	// public void bindMachineByPolicy(long MachineId, long policyId) throws FoundryException {
	// }
	//
	// public void unbindMachineByPolicy(long MachineId, long policyId) throws FoundryException {
	// }

	// *********************************************************************
	// A P P L I C A T I O N
	// *********************************************************************

//	public VoApp getAppById(long appId) throws TapirException {
//		try {
//			TransactionManager.transactionStart();
//			VoApp newApp = appDao.getAppByMid(appId);
//			TransactionManager.transactionCommit();
//			return newApp;
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
//	}
//
//	public List<VoApp> getAllApps() throws TapirException {
//		try {
//			TransactionManager.transactionStart();
//			List<VoApp> aList = appDao.getAllApps();
//			TransactionManager.transactionCommit();
//			return aList;
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
//	}

	//
	// Create, Update and Delete App
	//
//	public VoApp createApp(VoApp app) throws SQLException, TapirException {
//		try {
//			TransactionManager.transactionStart();
//			VoApp newApp = appDao.createApp(app);
//			TransactionManager.transactionCommit();
//			return newApp;
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
//	}

	public VoApp updateApp(VoApp app) throws SQLException, TapirException {
		return null;
	}

	public void deleteApp(long appId) throws SQLException, TapirException {
	}

	//
	// App and Group Relationship
	//
	public List<VoApp> getAppsByGroup(long groupId) throws TapirException {
		return null;
	}

//	public void bindAppByGroup(long appId, long groupId) throws TapirException {
//		try {
//			TransactionManager.transactionStart();
//			appDao.bindAppByGroup(appId, groupId);
//			TransactionManager.transactionCommit();
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
//	}

	public void unbindAppByGroup(long appId, long groupId) throws TapirException {
	}

	//
	// App and Policy Relationship
	//

	// public List<VoApp> getAppsByPolicy(long policyId) throws FoundryException {
	// return null;
	// }
	//
	// public void bindAppByPolicy(long AppId, long policyId) throws FoundryException {
	// }
	//
	// public void unbindAppByPolicy(long AppId, long policyId) throws FoundryException {
	// }

	// *********************************************************************
	// P O L I C Y
	// *********************************************************************

	//
	// Get Policy in General
	//

	// Get a particular policy by policy id
	public VoPolicy getPolicyById(String policyId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	public List<VoPolicy> getAllPolicies(Boolean fLoadRules) throws TapirException {
		return null;
	}

	// action can be attach, detatch, clone, snapshot ...
	public List<VoPolicy> getPoliciesByAction(String action, Boolean fLoadRules) throws TapirException {
		return null;
	}

	//
	// Create, Update and Delete Group
	//
	public VoPolicy createPolicy(VoPolicy policy) throws SQLException, TapirException {
		try {
			TransactionManager.transactionStart();

			// create parent policy
			VoPolicy newPolicy = null; //policyDao.createPolicy(policy);
			Long newPolicyId = newPolicy.getSid();

			// get old rule List
			List<VoRule> newRuleList = new ArrayList<VoRule>();
			for (VoRule rule : policy.getRuleList()) {
				// create rule entity, bind policy and rule together
				VoRule newRule = ruleDao.createRule(rule, newPolicyId);
				newRuleList.add(newRule);
				ruleDao.bindRuleByPolicy(newRule.getSid(), newPolicyId);
			}

			TransactionManager.transactionCommit();
			return newPolicy;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	// GKO - Not Test Yet
	public VoPolicy updatePolicy(VoPolicy policy) throws SQLException, TapirException {
		try {
			TransactionManager.transactionStart();

			// create parent policy
			Long oldPolicyId = policy.getSid();
			VoPolicy newPolicy = null; // policyDao.createPolicy(policy);
			Long newPolicyId = newPolicy.getSid();

			// get Old rule list
			List<VoRule> oldRuleList = ruleDao.getRulesByPolicyId(oldPolicyId);
			List<VoRule> updatedRuleList = compareRules(oldRuleList, policy.getRuleList());

			// get old rule List
			List<VoRule> newRuleList = new ArrayList<VoRule>();
			for (VoRule rule : updatedRuleList) {
				long rid = rule.getSid();
				if (rid == 0) {
					// this is a new rule, create a new rule record and bind it to new policy slave id
					VoRule newRule = ruleDao.createRule(rule, newPolicyId);
					newRuleList.add(newRule);
				} else {
					// bind existing rule to new policy slave id
					ruleDao.bindRuleByPolicy(rid, newPolicyId);
					newRuleList.add(rule);
				}
			}

			TransactionManager.transactionCommit();
			return newPolicy;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	private List<VoRule> compareRules(List<VoRule> oldRuleList, List<VoRule> newRuleList) {
		// for each new rule
		// 1. check newRule.sid against oldRuleList
		// 2. If oldRule and newRule are the same, keep newRule.sid
		// 3. If they are different, reset newRule.sid = 0
		// 4. return newRuleList

		return null;
	}

	public void deletePolicy(long policyId) throws SQLException, TapirException {
	}

	//
	// Policy and User with Group Relationship
	//

	// Get all policies for a particular user and groups he/she belong to
	public List<VoPolicy> getAllPoliciesByUserWithGroup(long userId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	// GKO Note
	// load entities individually to simplify the cache functionalities in the future.
	// avoid complicated joined SQL statements
	public List<VoPolicy> getAllPoliciesByUserWithGroupAndAction(long userId, String action, Boolean fLoadRules) throws TapirException {
		try {
			TransactionManager.transactionStart();

			Set<VoPolicy> pSet = new HashSet<VoPolicy>();
			// create parent policy
			List<VoPolicy> pList = null; // userDao.getAllPoliciesByUserWithGroupAndAction(userId, action);
			if (pList != null) {
				for (VoPolicy policy : pList) {
					pSet.add(policy);
				}
			}

			// find groups from user
			List<VoGroup> gList = null; //userDao.getAllGroupsByUser(userId);
//			if (gList != null) {
//				for (VoGroup group : gList) {
//				//	List<VoPolicy> pgList = groupDao.getAllPoliciesByGroup(group.getId());
//					if (pgList != null) {
//						for (VoPolicy policy : pgList) {
//							pSet.add(policy);
//						}
//					}
//				}
//			}

			for (VoPolicy policy : pSet) {
				// create rule entity, bind policy and rule together
				List<VoRule> rList = ruleDao.getRulesByPolicyId(policy.getSid());
				policy.setRuleList(rList);
			}

			TransactionManager.transactionCommit();
			return pList;
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
	}

	//
	// Policy and User Relationship
	//

	// Get all policies for an user
	public List<VoPolicy> getPoliciesByUser(String userId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	public List<VoPolicy> getPoliciesByUserAndAction(String userId, String action, Boolean fLoadRules) throws TapirException {
		return null;
	}

	//
	// Policy and Group Relationship
	//

	// Get all policies for a group
	public List<VoPolicy> getPoliciesByGroup(long groupId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	public List<VoPolicy> getPoliciesByGroupAndAction(long groupId, String action, Boolean fLoadRules) throws TapirException {
		return null;
	}

	// *********************************************************************
	// R U L E
	// *********************************************************************

	//
	// Get Rule in General
	//

	public VoRule getRuleById(String ruleId) throws TapirException {
		return null;
	}

	// Get rules by a policy Id
	public List<VoRule> getRulesByPolicy(long policyId) throws TapirException {
		return null;
	}

	// *********************************************************************
	// M E T A D A T A
	// *********************************************************************

	public void createAttributeMeta(VoMetaOperand attr) {
		// TODO Auto-generated method stub

	}

	public void createOperatorMeta(VoMetaOperator oper) {
		// TODO Auto-generated method stub

	}

	//
	// Create, Update and Delete Rule
	//

	// need to have a valid policy context first. if create at the same time with policy, then need to bind later on
	// public VoRule createRule(long policyId, VoRule rule) throws SQLException, FoundryException {
	// return null;
	// }
	//
	// public VoRule updateRule(long policyId, VoRule rule) throws SQLException, FoundryException {
	// return null;
	// }
	//
	// public void deleteRule(long ruleId) throws SQLException, FoundryException {
	// }
	//
	// // used after createRule when policyId is not created yet
	// public void bindRuleByPolicy(long policyId, long ruleId) throws FoundryException {
	// }

}
