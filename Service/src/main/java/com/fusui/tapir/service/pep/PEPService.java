package com.fusui.tapir.service.pep;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoApp;
import com.fusui.tapir.common.dto.VoCriteria;
import com.fusui.tapir.common.dto.VoDictionary;
import com.fusui.tapir.common.dto.VoExpNode;
import com.fusui.tapir.common.dto.VoMetaOperator;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.service.dao.DaoSequence;
import com.fusui.tapir.service.pap.PAPService;
import com.fusui.tapir.service.pdp.PDPService;
import com.fusui.tapir.service.pip.PIPService;

/**
 * provide the starting point for request evaluation.
 * 
 * @author Gordon Ko
 */
public class PEPService {

	private AtomicInteger uuid = new AtomicInteger(0);
	
	private static PEPService instance = new PEPService();

	public static PEPService getInstance() {
		return instance;
	}

	private PEPService() {
	}

	public boolean checkAccess(long userId, String action, String objType, long objId) throws TapirException, SQLException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

		// collect all policies of this user
		List<VoPolicy> policyList = PAPService.getInstance().getAllPoliciesByUserWithGroupAndAction(userId, action, true);
		
		// prepare policies, setup expression tree
		policyList = preparePolicies (policyList);
		
		// call PIP to resolve metadata embedded in the policy
		policyList = PIPService.getInstance().resolvePolicies(userId, action, objType, objId, policyList);

		// call PDP to make decision
		boolean result = PDPService.getInstance().evaluatePolicies(userId, policyList);

		return result;
	}

	public List<Boolean> checkAccess(long userId, String action, List<VoApp> resourceList) throws TapirException {
		return null;
	}


	private List<VoPolicy> preparePolicies (List<VoPolicy> policyList) throws SQLException, TapirException {
		// iterate each rule
		for (VoPolicy policy :policyList) {
			List <VoRule> rList = policy.getRuleList();
			for (VoRule rule : rList) {
				prepareRule (rule);
			}
		}
		return policyList;
	}
	
	/*
	 *  Create expression tree
	 */
	private static Map<String, VoMetaOperator> operMap = null;
	private synchronized static Map<String, VoMetaOperator> getOperatorMetaMap() throws TapirException {
		if (operMap == null) {
			PAPService pap = PAPService.getInstance();
			VoDictionary dictionary = pap.getDictionary();
			operMap = FoundryUtil.buildOperatorMapByVN(dictionary.getOperMap());
		}
		return operMap;
	}

	private static int getNumberOfOperands(String operator) throws TapirException {
		if (operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_AND) || operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_OR)) {
			return 2;
		}

		else if (operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_NOT)) {
			return 1;
		}

		throw new TapirException("unknown operator=" + operator);
	}

	private static boolean isLogicalOp(String operator) {
		return operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_AND) || operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_OR) || operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_NOT);
	}
	
	private static int checkPriority(String operator) {
		// operator precedence (hi 2 lo). Execution from left 2 right for operator with same priority
		// *, /
		// +, -
		// >, >=, <, <=
		// =, !=
		// AND
		// OR
		// NOT
		if (operator.equals("*") || operator.equals("/")) {
			return 7;
		}

		if (operator.equals("+") || operator.equals("-") || operator.equals("`") || operator.equals("!")) {
			return 6;
		}

		if (operator.equals(">") || operator.equals(">=") || operator.equals("<") || operator.equals("<=")) {
			return 5;
		}

		if (operator.equals("=") || operator.equals("!=")) {
			return 4;
		}

		if (operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_AND)) {
			return 3;
		}

		if (operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_OR)) {
			return 2;
		}

		if (operator.equalsIgnoreCase(FoundryConstants.RULE_OPERATOR_NOT)) {
			return 1;
		}

		return 0;
	}
	
	private VoRule prepareRule (VoRule rule) throws SQLException, TapirException {

		List <String> criteriaList = rule.getCriteriaList();
		Map<String, VoMetaOperator> opMap = getOperatorMetaMap();

		// convert the criteriaList from infix to postfix
		Stack<VoCriteria> stack = new Stack<VoCriteria>();
		List<VoCriteria> postfix = new ArrayList<VoCriteria>();
		for (String strCriteria : criteriaList) {
			
			VoCriteria criteria = new VoCriteria (strCriteria);
			String operator = criteria.getOperator();
			if (operator.equals("(")) {
				stack.push(criteria);
			} else if (operator.equals(")")) {
				// move criteria from stack to postfix list
				while ((stack.peek().getOperator()).charAt(0) != '(') {
					postfix.add(stack.pop());
				}
				stack.pop();
			} else if (isLogicalOp(operator)) {
				while (!stack.empty() && !(stack.peek().getOperator()).equals("(") && checkPriority(operator) <= checkPriority(stack.peek().getOperator())) {
					postfix.add(stack.pop());
				}
				stack.push(criteria);
			} else {
				// should be valid for any other criteria
				postfix.add(criteria);
			}
		}

		while (!stack.empty()) {
			postfix.add(stack.pop());
		}

		// If criteria is not a logic op, convert it into 3 nodes. The 'operator' will be the parent node, 
		// the 'attrName' will be the first child node and 'attrValue' will be the second child node.
		// Push the parent node ('operator') to stack
		// If criteria is a logic op, create a new node, popup 2 nodes (one node for Not) from stack and add it to the new node childList. Push the new node to stack

		// The left node (attrName) will have a smaller node ID and right node will have a bigger one.
		// The node list will be retrieved from DB by node_id order (see RuleServices.loadExpressionTree foe details) and add to childList

		Stack<VoExpNode> nodeStack = new Stack<VoExpNode>();
		List<VoExpNode> nodeList = new ArrayList<VoExpNode>();
		VoExpNode rootNode = null;

		for (VoCriteria criteria : postfix) {
			String operator = criteria.getOperator();
			if (isLogicalOp(operator)) {

				if (nodeStack.isEmpty()) {
					// ignore this logic op since nothing in stack. Something wrong
					new Exception().printStackTrace();
					continue;
				}

				int nOperand = getNumberOfOperands(operator);
				if (nodeStack.size() < nOperand) {
					throw new TapirException("not enough operand for operator=" + operator);
				}

				String pid = Integer.toString(uuid.incrementAndGet());
				VoExpNode pNode = new VoExpNode();
				pNode.setNodeId(pid);
				pNode.setNodeType(FoundryConstants.RULE_NODE_TYPE_OPERATOR);
				pNode.setValue(operator);
				pNode.setValueType(FoundryConstants.RULE_VALUE_TYPE_BOOLEAN);

				for (int i = 0; i < nOperand; i++) {
					VoExpNode opNode = nodeStack.pop();
					opNode.setParentId(pid);
				}

				nodeList.add(pNode);
				nodeStack.push(pNode);

				// the last one will be the root node since it is in postfix order
				rootNode = pNode;

			} else {

				String type = criteria.getAttrType();

				// parent node
				String pid = DaoSequence.getInstance().getUniqueId().toString();
				VoExpNode pNode = new VoExpNode();
				pNode.setNodeId(pid);
				pNode.setNodeType(FoundryConstants.RULE_NODE_TYPE_OPERATOR);
				pNode.setValue(operator);
				pNode.setValueType(type);

				// first child node, attr name
				VoExpNode c1Node = new VoExpNode();
				c1Node.setNodeId(DaoSequence.getInstance().getUniqueId().toString());
				c1Node.setNodeType(FoundryConstants.RULE_NODE_TYPE_OPERAND);
				c1Node.setValue(criteria.getAttrName());
				c1Node.setValueType(type);
				c1Node.setParentId(pid);
				nodeList.add(c1Node);

				// second child node, attr value, can be optional
				String key = FoundryUtil.createOperatorKeyByVN(type, operator);
				VoMetaOperator operMeta = opMap.get(key);
				int nOperand = operMeta.getNumOfOperands();
				if (nOperand > 1) {
					VoExpNode c2Node = new VoExpNode();
					c2Node.setNodeId(DaoSequence.getInstance().getUniqueId().toString());
					c2Node.setNodeType(FoundryConstants.RULE_NODE_TYPE_OPERAND);
					c2Node.setValue(criteria.getAttrValue());
					c2Node.setValueType(type);
					c2Node.setParentId(pid);
					nodeList.add(c2Node);
				}

				// add parent the last
				nodeList.add(pNode);

				// push pNode into stack since its parentNode is not setup yet, everything under pNode is setup completely
				nodeStack.push(pNode);

				// the last one will be the root node since it is in postfix order (in case only one criteria and no logic op in the list)
				rootNode = pNode;

			}
		}

		rootNode.setParentId(FoundryConstants.RULE_NODE_ROOT_ID);
		rule.setExpRootNode(rootNode);
		rule.setExpNodeList(nodeList);
		return rule;
	}


}
