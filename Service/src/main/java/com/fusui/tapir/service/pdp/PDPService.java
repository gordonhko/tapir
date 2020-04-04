package com.fusui.tapir.service.pdp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.FoundryDateTimeFormatter;
import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoDictionary;
import com.fusui.tapir.common.dto.VoExpNode;
import com.fusui.tapir.common.dto.VoMetaOperator;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.service.pap.PAPService;
import com.fusui.tapir.service.pdp.operator.IOperator;

public class PDPService {
	
	private static Logger logger = LoggerFactory.getLogger(PDPService.class);
	private static PDPService instance = new PDPService();

	//private VoDictionary dictionary = null;
	private Map <String, Class<IOperator>> classMap = new HashMap<String, Class<IOperator>>();
	private Map <String, Method> methodMap = new HashMap<String, Method>();
	private Map <String, IOperator> instanceMap = new HashMap<String, IOperator>();

	private static Map<String, VoMetaOperator> operMap = null;
	private synchronized static Map<String, VoMetaOperator> getOperatorMetaMap() throws TapirException {
		if (operMap == null) {
			PAPService pap = PAPService.getInstance();
			VoDictionary dictionary = pap.getDictionary();
			operMap = FoundryUtil.buildOperatorMapByVN(dictionary.getOperMap());
		}
		return operMap;
	}
	

	public static PDPService getInstance() {
		return instance;
	}

	private PDPService() {
	}


	public boolean evaluatePolicies(long userId, List<VoPolicy> policyList) throws TapirException {
	
		boolean fOK = false;
		for (VoPolicy policy : policyList) {
			List<VoRule> list = policy.getRuleList();
			for (VoRule rule : list) {
				if (  evaluateRule (rule) ) {
					// for testing purpose, any ok is ok and should return immediately
					fOK = true;
				}
			}
		}
		
		return fOK;
	}
	

	private Class findClass(String className) throws ClassNotFoundException {
		Class result = classMap.get(className);
		if (result == null) {
			synchronized (this) {
				result = classMap.get(className);
				if (result == null) {
					result = Class.forName(className);
					classMap.put(className, result);
				}
			}
		}
		return result;
	}
	
	private IOperator findInstance(VoMetaOperator operatorMeta) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String className = operatorMeta.getClassName();
		IOperator result = instanceMap.get(className);
		if (result == null) {
			synchronized (this) {
				result = instanceMap.get(className);
				if (result == null) {
					Class clazz = findClass(className);
					result = (IOperator) clazz.newInstance();
					instanceMap.put(className, result);
				}
			}
		}
		return result;
	}
	
	private Method findMethod(VoMetaOperator operatorMeta, Class[] paramTypes) throws ClassNotFoundException, NoSuchMethodException {
		String className = operatorMeta.getClassName();
		String methodName = operatorMeta.getMethodName();
		
		Method result = methodMap.get(className+methodName);
		if (result == null) {
			synchronized (this) {
				result = methodMap.get(className+methodName);
				if (result == null) {
					Class clazz = findClass(className);
					result = clazz.getMethod(methodName, paramTypes);
					methodMap.put(className+methodName, result);
				}
			}
		}
		return result;
	}
	
	
	private boolean evaluateRule(VoRule rule) throws TapirException {
		try {
//			rule = buildExpressionTree(rule);
//			List <VoExpNode> nodeList = convert2Posfix(rule);
//			return evaluatePostfix(nodeList);
			
			// expNodeList is in Postfix already
			// check PAPService.prepareRule
			boolean fOK =  evaluatePostfix(rule.getExpNodeList());
			logger.info("evaluate rule is "+fOK);
			return fOK;
			
		}
		catch (Throwable t) {
			throw new TapirException (t);
		}
	}
	
private boolean evaluatePostfix(List<VoExpNode> nodeList) throws TapirException, ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException {
		
		Stack <VoExpNode> stack = new Stack <VoExpNode> ();
		for (VoExpNode node : nodeList) {
			
			logger.info("--> node="+node);
			
			if (node.getNodeType().equals(FoundryConstants.RULE_NODE_TYPE_OPERAND )){
				stack.push(node);	
			}
			
			else if (node.getNodeType().equals(FoundryConstants.RULE_NODE_TYPE_OPERATOR )) {
				
				// ValueType and Value embedded in VoExpNode must be copied from System dictionary 
				//VoOperatorMeta operatorMeta = findOperator (node.getValueType(), node.getValue());
				String key = FoundryUtil.createOperatorKeyByVN (node.getValueType(), node.getValue() );
				VoMetaOperator operatorMeta = getOperatorMetaMap().get(key);
				
				int nParameter = operatorMeta.getNumOfOperands();
				Object[] paramValues = new Object[nParameter];
				Class[] paramTypes = new Class[nParameter];
				
				// when pop from stack, the first one will be the last parameter
				for (int i=nParameter-1; i>=0; i-- ) {
					
					VoExpNode opNode = stack.pop();
					String value = opNode.getValue();
					String valueType = opNode.getValueType();
					
					if (valueType.equals(FoundryConstants.DATA_TYPE_STRING)) {
						paramValues[i] = value;
					}
					else if (valueType.equals(FoundryConstants.DATA_TYPE_INTEGER)) {
						paramValues[i] = Integer.parseInt(value);
						
					}
					else if (valueType.equals(FoundryConstants.DATA_TYPE_BOOLEAN)) {
						paramValues[i] = Boolean.parseBoolean(value);
					}
					else if (valueType.equals(FoundryConstants.DATA_TYPE_DATETIME)) {
						//value is normalized into a fixed length string in long format
						paramValues[i] = FoundryDateTimeFormatter.getDateByMilliseconds(value);
					}
					else if (valueType.equals(FoundryConstants.DATA_TYPE_DOUBLE)) {
						paramValues[i] = Double.parseDouble(value);
					}
					
					paramTypes[i] = paramValues[i].getClass();
				}
				
				Method method = findMethod(operatorMeta, paramTypes);
				IOperator instance = findInstance(operatorMeta);
				Object resultObj = method.invoke(instance, paramValues);

				VoExpNode resultNode = new VoExpNode();
				resultNode.setNodeType(FoundryConstants.RULE_NODE_TYPE_OPERAND);
				resultNode.setValue(resultObj.toString());
				
				String returnedType = method.getReturnType().getSimpleName();
				resultNode.setValueType(returnedType);
//				resultNode.setValueType(operatorMeta.getReturnedType());
				
				stack.push(resultNode);
			}
			
			else {
				String msg = "Traverse tree error since node type is invalid nodeId=" + node.getNodeId() +" nodeType=" + node.getNodeType();
				logger.error(msg);
				throw new TapirException(msg);
			}
			
		}
		
		if (stack.size() != 1 ) {
			StringBuilder sb = new StringBuilder();
			for (VoExpNode node : stack) {
				if (sb.length() > 0) {
					sb.append(",");
				}
				sb.append(node.getNodeType()+":"+node.getValue()+node.getValueType()+"\n");
			}
			String msg = "Evaluation Error, more than 1 node in the stack size=" + stack.size() + "\n" + sb.toString();
			logger.error(msg);
			throw new TapirException(msg);
		}

		VoExpNode result = stack.pop();
		if (result.getNodeType() != FoundryConstants.RULE_NODE_TYPE_OPERAND && result.getValueType() != FoundryConstants.DATA_TYPE_BOOLEAN ) {
			String msg = "Evaluation Error, the result node is not operand and value type is not boolean type=" + result.getNodeType() + " value="+result.getValue()+" valueType="+result.getValueType();
			logger.error(msg);
			throw new TapirException(msg);
		}
		
		return Boolean.parseBoolean(result.getValue());
	}
	
	
	private VoRule buildExpressionTree(VoRule rule) throws TapirException {
		
		VoExpNode oldRootNode = rule.getExpRootNode();
		if (null != oldRootNode ) {
			String msg = "The rootNode is setup already ruleId=" + rule.getSid();
			logger.info(msg);
			if (null != oldRootNode.getChildList() && oldRootNode.getChildList().size() > 0 ) {
				msg = "The rootNode has child list ruleId=" + rule.getSid();
				logger.info(msg);
			}
		}

		// rebuild the tree based on node list, the child list need to rebuild
		List <VoExpNode> nodeList = rule.getExpNodeList();
		Map<String, VoExpNode> map = new HashMap<String, VoExpNode>();
		for (VoExpNode node : nodeList) {
			// reset the child list
			if (node.getChildList() != null && node.getChildList().size() > 0) {
				System.out.println("---------> non-empty root node="+node.getNodeId());
			}
			//node.setChildList(null);
			String nid = node.getNodeId();
			map.put(nid, node);
		}
		
		VoExpNode rootNode = null;
		for (VoExpNode node : nodeList) {
			String pid = node.getParentId();

			if (pid.equals(FoundryConstants.RULE_NODE_ROOT_ID)) {
				if ( oldRootNode != null && ! oldRootNode.getNodeId().equals(node.getNodeId()) ) {
					String msg = "Cannot build tree since  mismatched root id=" +  rule.getExpRootNode().getNodeId() +" node id="+node.getNodeId();
					logger.error(msg);
					throw new TapirException(msg);
				}
				// refresh the root node
				rootNode = node;
				continue;
			}

			VoExpNode pNode = map.get(pid);
			if (null == pNode) {
				String msg = "Cannot build tree since parent node is not available ruleId=" + rule.getSid()+" parentId="+pid;
				logger.error(msg);
				throw new TapirException(msg);
			}
			List<VoExpNode> list = pNode.getChildList();
			
			// the child is added without order, it will up to caller to decide which node is in order. 
			// in general, the child node in the left will have a smaller node id (generated by sequence number)
			list.add(node);
		}

		if (null == rootNode) {
			String msg = "Cannot build tree since root node is not found ruleId=" + rule.getSid();
			logger.error(msg);
			throw new TapirException(msg);
		}
		
		rule.setExpRootNode(rootNode);
		return rule;
	}
	
	private List <VoExpNode> convert2Posfix(VoRule rule) throws TapirException {
		List <VoExpNode> result = new ArrayList<VoExpNode>();
		traverseNode (rule.getExpRootNode(), result);
		return result;
	}
		
	private void traverseNode(VoExpNode node, List<VoExpNode> result) throws TapirException {
		if (null == node ) {
			return;
		}

		// the childList is sorted by nodeId
		List <VoExpNode> childList = node.getChildList();		
		int currentId = -1;
		
		for (VoExpNode childNode : childList) {
			int nid = Integer.parseInt(childNode.getNodeId());
			if (nid <= currentId ) {
				String msg = "Traverse tree error since node id is out of order parent nodeId=" + node.getNodeId();
				logger.warn(msg);
				
				// criteria from queryprocessor are not sorted
				throw new TapirException(msg);
			}
			currentId = nid;
			traverseNode (childNode, result);
		}
		
		result.add(node);
	}
	
//	private VoOperatorMeta findOperator (String key, String operator) throws FoundryException {
//		
//		VoOperatorMetaList list = dictionary.getOperMap().get(key);
//		
//		if (list != null) {
//			for (VoOperatorMeta op : list.getList()) {
//				if (op.getOperatorName().equals(operator)) {
//					return op;
//				}
//			}
//		}
//		
//		// it should never happen
//		String msg = "Cannot find operator dataType=" + key + " operator="+operator;
//		logger.error(msg);
//		throw new FoundryException(msg);
//		
//	}
	
	
}
