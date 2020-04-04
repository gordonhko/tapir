// 1. add operator metadata
// 2. refactory VoDictionary.
// 3. remove static operMap at PEPService and PDPService
// 4. add function for test
// 5. PDPService need to be stateless
// 6. PIPService need to handle more different resources (App, Machine ...) in a generic approach

package com.fusui.tapir.service.acs;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoApp;
import com.fusui.tapir.common.dto.VoGroup;
import com.fusui.tapir.common.dto.VoMachine;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.common.dto.VoUser;
import com.fusui.tapir.service.pap.PAPService;
import com.fusui.tapir.service.pep.PEPService;

@Ignore
public class TestSchema {

	private static Logger logger = LoggerFactory.getLogger(TestSchema.class);

	private static TestSchema s_singleton = new TestSchema();

	public static TestSchema getInstance() {
		return s_singleton;
	}

	private TestSchema() {
	}

	// **********************************************************************
	// Create Department
	// **********************************************************************

	private VoGroup createGroup(String name, String type) throws TapirException, SQLException {
		PAPService pap = PAPService.getInstance();
		VoGroup group = new VoGroup();
		group.setName(name);
		group.setDesc(name);
		group.setType(type);
		return pap.createGroup(group);
	}

	// **********************************************************************
	// Create User
	// **********************************************************************
	private VoUser createUser(String firstName, String lastName, String loginName, String password, String department) throws Exception {
		PAPService pap = PAPService.getInstance();
		VoUser user = new VoUser();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setLoginName(loginName);
		user.setPassword(password);
		user.setDepartment(department);
		user.setActive(1);
		return pap.createUser(user);
	}

	private void bindUserAndGroup(VoUser user, VoGroup group) throws TapirException {
		PAPService pap = PAPService.getInstance();
		pap.bindUserByGroup(user.getMid(), group.getId());
	}

	// **********************************************************************
	// Create Machine
	// **********************************************************************
//	private VoMachine createMachine(String name, String description, long owner) throws Exception {
//		PAPService pap = PAPService.getInstance();
//		VoMachine machine = new VoMachine();
//		machine.setName(name);
//		machine.setDesc(description);
//		machine.setOwner(owner);
//		return pap.createMachine(machine);
//	}
//
//	private void bindMachineAndGroup(VoMachine machine, VoGroup group) throws TapirException {
//		PAPService pap = PAPService.getInstance();
//		pap.bindMachineByGroup(machine.getMid(), group.getId());
//	}
//
//	// **********************************************************************
//	// Create Machine
//	// **********************************************************************
//	private VoApp createApp(String name, String description, long parentId, int maxCopy, Date expired, long owner) throws Exception {
//		PAPService pap = PAPService.getInstance();
//		VoApp app = new VoApp();
//		app.setName(name);
//		app.setDesc(description);
//		app.setParentId(parentId);
//		app.setMaxCopy(maxCopy);
//		app.setDeathday(expired);
//		app.setOwner(owner);
//		return pap.createApp(app);
//	}
//
//	private void bindAppAndGroup(VoApp app, VoGroup group) throws TapirException {
//		PAPService pap = PAPService.getInstance();
//		pap.bindAppByGroup(app.getMid(), group.getId());
//	}

	// **********************************************************************
	// Create Policy
	// **********************************************************************

	private VoPolicy createPolicy(String name, String desc, String action, String type, List<VoRule> ruleList) throws TapirException, SQLException {
		VoPolicy policy = new VoPolicy();
		policy.setName(name);
		policy.setDescription(desc);
		policy.setType(type);
		policy.setAction(action);
		policy.setRuleList(ruleList);
		PAPService pap = PAPService.getInstance();
		pap.createPolicy(policy);
		return policy;
	}

	private String buildCriteria(String attrName, String attrValue, String operator, String valueType) {
		ByteBuffer buffer = ByteBuffer.allocate(attrName.length() + attrValue.length() + operator.length() + valueType.length() + 3);
		buffer.put(attrName.getBytes());
		buffer.put((byte) FoundryConstants.DELIMITER_CRITERIA_OP);
		buffer.put(attrValue.getBytes());
		buffer.put((byte) FoundryConstants.DELIMITER_CRITERIA_OP);
		buffer.put(operator.getBytes());
		buffer.put((byte) FoundryConstants.DELIMITER_CRITERIA_OP);
		buffer.put(valueType.getBytes());
		return new String(buffer.array());
	}

	private void initUsers() throws Exception {

		// create groups
		VoGroup g1 = createGroup("Admin", "$USER_GROUP");
		VoGroup g2 = createGroup("DBA", "$USER_GROUP");
		VoGroup g3 = createGroup("Architect", "$USER_GROUP");

		// create users
		VoUser u1 = createUser("Frank", "Lai", "flai", "password", "San Jose");
		VoUser u2 = createUser("Gordon", "Ko", "gko", "password", "San Jose");
		VoUser u3 = createUser("CP", "Cehn", "cchen", "password", "San Jose");

		bindUserAndGroup(u1, g1);
		bindUserAndGroup(u1, g2);
		bindUserAndGroup(u1, g3);

		bindUserAndGroup(u2, g2);

		bindUserAndGroup(u3, g3);
	}

	private void initResources() throws Exception {

//		// create groups
//		VoGroup g1 = createGroup("Database", "$APP_GROUP");
//		VoGroup g2 = createGroup("CRM", "$APP_GROUP");
//		VoGroup g3 = createGroup("ERP", "$APP_GROUP");
//
//		// create app
//		Date expired = new Date();
//		expired.setYear(expired.getYear() + 1);
//		VoApp a1 = createApp("OrFoundryUtile", "OrFoundryUtile", 0, -1, expired, 0);
//		VoApp a2 = createApp("MySQL", "MySQL", 0, -1, expired, 0);
//		VoApp a3 = createApp("SalesForce", "SalesForce", 0, -1, expired, 0);
//
//		bindAppAndGroup(a1, g1);
//		bindAppAndGroup(a2, g1);
//		bindAppAndGroup(a3, g2);
//		bindAppAndGroup(a3, g3);
//
//		// create groups
//		VoGroup g4 = createGroup("Windows", "$MACHINE_GROUP");
//		VoGroup g5 = createGroup("Linux", "$MACHINE_GROUP");
//
//		// create machine
//		VoMachine m1 = createMachine("CentOS", "CentOS", 0);
//		VoMachine m2 = createMachine("Ubuntu", "Ubuntu", 0);
//		VoMachine m3 = createMachine("Windows", "Windows", 0);
//
//		bindMachineAndGroup(m1, g4);
//		bindMachineAndGroup(m2, g4);
//		bindMachineAndGroup(m3, g5);

	}

	private void initPolicies() throws Exception {

		PAPService pap = PAPService.getInstance();

		//
		// Policy 1
		//
		String c1 = buildCriteria("$User.Active", "1", "Equal", "Integer");
		String c2 = buildCriteria("", "", "And", "Operator");
		String c3 = buildCriteria("$User.Status", "-1", "NotEqual", "Integer");
		VoRule rule = new VoRule();
		rule.setDescription("User is active");
		List<String> cList = new ArrayList<String>();
		cList.add(c1);
		cList.add(c2);
		cList.add(c3);
		rule.setCriteriaList(cList);
		List<VoRule> rList = new ArrayList<VoRule>();
		rList.add(rule);

		VoPolicy userActivePolicy = createPolicy("UserActive", "User need to be active", FoundryConstants.POLICY_ACTION_READ, "", rList);

		VoUser u1 = pap.getUserByLoginName("gko");
		pap.bindUserByPolicy(u1.getMid(), userActivePolicy.getMid());

		//
		// Policy 2
		//
		c1 = buildCriteria("$Env.CurrentTime", "09:00", "After", FoundryConstants.DATA_TYPE_DATETIME);
		c2 = buildCriteria("", "", "And", "Operator");
		c3 = buildCriteria("$Env.CurrentTime", "17:00", "Before", FoundryConstants.DATA_TYPE_DATETIME);
		String c4 = buildCriteria("", "", "And", "Operator");
		String c5 = buildCriteria("", "", "(", "");
		String c6 = buildCriteria("$User.UserDepartmentId", "Engineering", "In", "String");
		String c7 = buildCriteria("", "", "Or", "Operator");
		String c8 = buildCriteria("$User.UserDepartmentId", "CTO", "In", "String");
		String c9 = buildCriteria("", "", ")", "");
	}

	private void checkPolicies() throws TapirException, SQLException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//		PAPService pap = PAPService.getInstance();
//		VoUser u1 = pap.getUserByLoginName("gko");
//
//		List<VoApp> aList = pap.getAllApps();
//		VoApp a1 = pap.getAppById(aList.get(0).getMid());
//
//		PEPService pep = PEPService.getInstance();
//		boolean fOk = pep.checkAccess(u1.getMid(), "Read", "App", a1.getMid());

	}

	private void doTask() throws Exception {
		initUsers();
		initResources();
		initPolicies();

		checkPolicies();
	}

	public static void main(String[] args) throws Exception {
		TestSchema cliTest = new TestSchema();
		cliTest.doTask();
		System.out.println("Initialization of PM tables demo data completed.");
		System.exit(0);
	}

}
