package com.fusui.tapir.service.pip;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoApp;
import com.fusui.tapir.common.dto.VoExpNode;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.common.dto.VoUser;
import com.fusui.tapir.service.pap.PAPService;
import com.fusui.tapir.service.util.EvaluationCtx;


/**
 * provide the starting point for request evaluation. 
 *
 * @author Gordon Ko
 */
public class PIPService
{
	private static Logger logger = LoggerFactory.getLogger(PIPService.class);
	
	private static PIPService instance = new PIPService();

	public static PIPService getInstance() {
		return instance;
	}

	private PIPService() {
	}

	public List<VoPolicy> resolvePolicies(long userId, String action, String objType, long objId, List<VoPolicy> policyList) throws NoSuchMethodException, TapirException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		PAPService pap = PAPService.getInstance();
		VoUser user = pap.getUserById(userId);
		VoApp app = null; //pap.getAppById(objId);
				
		for (VoPolicy policy : policyList) {
			List<VoRule> list = policy.getRuleList();
			for (VoRule rule : list) {
				fillRule(rule, user, app);
			}
		}
		
		return policyList;
	}
	
	
	/*
	 * Fill in data
	 */
	private void fillRule(VoRule rule, VoUser user, VoApp metadata) throws IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, TapirException {
		List<VoExpNode> list = rule.getExpNodeList();
		for (VoExpNode obj : list) {
			fillExpNode(obj, user, metadata);
		}
	}

	private void fillExpNode(VoExpNode node, VoUser user, VoApp metadata) throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, TapirException {
		if (!node.getNodeType().equals(FoundryConstants.RULE_NODE_TYPE_OPERAND)) {
			return;
		}

		String value = node.getValue();
		logger.info("node.value=" + value);

		// subject, resource, action, environment
		if (value.startsWith(FoundryConstants.RULE_NODE_VARIABLE_USER)) { // "$User."
			String prefix = "get";
			String method = prefix + value.substring(FoundryConstants.RULE_NODE_VARIABLE_USER_LENGTH);
			logger.info("method =" + method);

			// see the VoUser pojo for the list of getters,
			// we support all getters, as long as it returns a String,
			// $User.XXX -> VoUser.getXXX

			Method mm = VoUser.class.getMethod(method, (Class<?>[])null);
			Object obj = mm.invoke(user, (Object[])null);
			// handle return value as Integer or other types
			// String attriValue = (String) obj;
			String attriValue = obj.toString();

			logger.info("method returns=" + attriValue);
			node.setValue(attriValue);
		}

		else if (value.startsWith(FoundryConstants.RULE_NODE_VARIABLE_APP)) { // "$App."

			if (metadata == null) {
				throw new TapirException (" No app instance for PIP to fill data");
			}

			String prefix = "get";
			String method = prefix + value.substring(FoundryConstants.RULE_NODE_VARIABLE_APP_LENGTH);
			logger.info("method =" + method);

			// see the VoMetadata pojo for the list of getters,
			// we support all getters, as long as it returns a String,
			// $Metadata.XXX -> VoMetadata.getXXX

			Method mm = VoApp.class.getMethod(method, (Class<?>[])null);
			Object obj = mm.invoke(metadata, (Object[])null);

			// String attriValue = (String) obj;
			String attriValue = obj.toString();

			logger.info("method returns=" + attriValue);
			node.setValue(attriValue);
		}

		else if (value.startsWith(FoundryConstants.RULE_NODE_VARIABLE_ENV)) { // "$Env."
			// ex. $Env.CurrentTime
			String prefix = "get";
			String method = prefix + value.substring(FoundryConstants.RULE_NODE_VARIABLE_ENV_LENGTH);
			logger.info("method =" + method);

			Method mm = EvaluationCtx.class.getMethod(method, (Class<?>[])null);
			Object obj = mm.invoke(new EvaluationCtx(), (Object[])null);

			// String attriValue = (String) obj;
			String attriValue = obj.toString();

			logger.info("method returns=" + attriValue);
			node.setValue(attriValue);
		}
	}
	
}
