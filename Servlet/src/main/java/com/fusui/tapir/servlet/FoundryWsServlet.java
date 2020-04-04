package com.fusui.tapir.servlet;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.FoundryProperties;
import com.fusui.tapir.common.FoundryVersion;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoApp;
import com.fusui.tapir.common.dto.VoGroup;
import com.fusui.tapir.common.dto.VoMachine;
import com.fusui.tapir.common.dto.VoPolicy;
import com.fusui.tapir.common.dto.VoRule;
import com.fusui.tapir.common.dto.VoUser;
import com.fusui.tapir.service.pap.PAPService;

/*
 * @author gko
 */

@Path(FoundryConstants.RESOURCE_ROOT)
public class FoundryWsServlet {

	private static Logger logger = LoggerFactory.getLogger(FoundryWsServlet.class);

	private static PAPService pap = PAPService.getInstance();

	public FoundryWsServlet() {

	}

	private Response createErrorResponse(Throwable t) throws WebApplicationException {
		logger.error(t.getMessage(), t);
		return new FoundryWsExceptionMapper().toResponse(t);
	}

	// **********************************************************************
	// C O M M O M
	// **********************************************************************

	@GET
	@Path("/version")
	@Produces(MediaType.APPLICATION_JSON)
	// in browser type http://localhost:8080/foundry/version
	public Response getVersion() {
		return Response.status(Status.OK).entity(FoundryVersion.FOUNDRY_VERSION).type(MediaType.APPLICATION_JSON).build();

	}

	@POST
	@Path("/hello")
	@Produces(MediaType.APPLICATION_JSON)
	// in browser type http://localhost:8080/foundry/version
	public Response getHello() {
		return Response.status(Status.OK).entity("Hello").type(MediaType.APPLICATION_JSON).build();
	}

	@POST
	@Path("/hellopost")
	@Produces(MediaType.APPLICATION_JSON)
	public Response helloMap(VoApp app) throws TapirException {
		try {
			System.out.println("app=" + app);
			VoApp result = app; // PAPService.getInstance().createApp(app);
			return Response.status(Status.OK).entity(result).type(MediaType.APPLICATION_JSON).build();
		} catch (Throwable t) {
			return createErrorResponse(t);
		}
	}

	@GET
	@Path("/properties")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProperties() {
		try {
			String result = FoundryProperties.getFoundryProperties().toString();
			System.out.println("properties -----> " + result);
			return Response.status(Status.OK).entity(result).type(MediaType.APPLICATION_JSON).build();
		} catch (Throwable t) {
			t.printStackTrace();
			return createErrorResponse(t);
		}
	}

	// **********************************************************************
	// U S E R
	// **********************************************************************

	// **********************************************************************
	// Create, Update and Delete User
	// **********************************************************************
	public VoUser createUser(VoUser user) throws TapirException {
		return null;
	}

	public VoUser updateUser(VoUser user) throws TapirException {
		return null;
	}

	public void deleteUser(long userId) throws TapirException {
	}

	// **********************************************************************
	// Get User
	// **********************************************************************
	@GET
	@Path("/user/{userId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("userId") String userId) throws TapirException {
		try {
			System.out.println("getUserById-> id=" + userId);
			VoUser result = pap.getUserById(Long.parseLong(userId));
			return Response.status(Status.OK).entity(result).type(MediaType.APPLICATION_JSON).build();
		} catch (Throwable t) {
			t.printStackTrace();
			return createErrorResponse(t);
		}
	}

	public List<VoUser> getAllUsers() throws TapirException {
		return null;
	}

	// **********************************************************************
	// User and Group Relationship
	// **********************************************************************
	public List<VoUser> getUsersByGroup(long groupId) throws TapirException {
		return null;
	}

	public void bindUserByGroup(long userId, long groupId) throws TapirException {
	}

	public void unbindUserByGroup(long userId, long groupId) throws TapirException {
	}

	// **********************************************************************
	// User and Policy Relationship
	// **********************************************************************
	public List<VoUser> getUsersByPolicy(long policyId) throws TapirException {
		return null;
	}

	public void bindUserByPolicy(long userId, long policyId) throws TapirException {
	}

	public void unbindUserByPolicy(long userId, long policyId) throws TapirException {
	}

	// **********************************************************************
	// A P P L I C A T I O N
	// **********************************************************************

	// **********************************************************************
	// Create, Update and Delete Application
	// **********************************************************************
	@POST
	@Path("/app/{userId}/{appName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createApp(@PathParam("userId") String userId, @PathParam("appName") String appName, VoApp app) throws TapirException {
		try {
			System.out.println("createApp -----> userId=" + userId + ", name=" + appName + ", app=" + app);
			VoApp result = app; // PAPService.getInstance().createApp(app);
			return Response.status(Status.OK).entity(result).type(MediaType.APPLICATION_JSON).build();
		} catch (Throwable t) {
			return createErrorResponse(t);
		}
	}

	public VoApp updateApp(VoApp app) throws TapirException {
		return null;
	}

	public void deleteApp(long appId) throws TapirException {
	}

	// **********************************************************************
	// Get Application
	// **********************************************************************

	public VoApp getAppById(long appId) throws TapirException {
		return null;
	}

	public List<VoApp> getAllApps() throws TapirException {
		return null;
	}

	// **********************************************************************
	// Application and Group Relationship
	// **********************************************************************
	public List<VoApp> getAppsByGroup(long groupId) throws TapirException {
		return null;
	}

	public void bindAppByGroup(long appId, long groupId) throws TapirException {
	}

	public void unbindAppByGroup(long appId, long groupId) throws TapirException {
	}

	// **********************************************************************
	// M A C H I N E
	// **********************************************************************

	// **********************************************************************
	// Create, Update and Delete Machine
	// **********************************************************************
	public VoMachine createMachine(VoMachine machine) throws TapirException {
		return null;
	}

	public VoMachine updateMachine(VoMachine machine) throws TapirException {
		return null;
	}

	public void deleteMachine(long machineId) throws TapirException {
	}

	// **********************************************************************
	// Get Machine
	// **********************************************************************
	public VoMachine getMachineById(long machineId) throws TapirException {
		return null;
	}

	public List<VoMachine> getAllMachines() throws TapirException {
		return null;
	}

	// **********************************************************************
	// Machine and Group Relationship
	// **********************************************************************
	public List<VoMachine> getMachinesByGroup(long groupId) throws TapirException {
		return null;
	}

	public void bindMachineByGroup(long machineId, long groupId) throws TapirException {
	}

	public void unbindMachineByGroup(long machineId, long groupId) throws TapirException {
	}

	// **********************************************************************
	// G R O U P
	// **********************************************************************

	// **********************************************************************
	// Create, Update and Delete Group
	// **********************************************************************
	public VoGroup createGroup(VoGroup group) throws TapirException {
		return null;
	}

	public VoGroup updateGroup(VoGroup group) throws TapirException {
		return null;
	}

	public void deleteGroup(long groupId) throws TapirException {
	}

	// **********************************************************************
	// Get Group
	// **********************************************************************
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

	// **********************************************************************
	// Group and User Relationship
	// **********************************************************************

	public List<VoGroup> getGroupsByUser(long userId) throws TapirException {
		return null;
	}

	// **********************************************************************
	// Group and Policy Relationship
	// **********************************************************************

	public List<VoUser> getGroupsByPolicy(long policyId) throws TapirException {
		return null;
	}

	public void bindGroupByPolicy(long groupId, long policyId) throws TapirException {
	}

	public void unbindGroupByPolicy(long groupId, long policyId) throws TapirException {
	}

	// **********************************************************************
	// P O L I C Y
	// **********************************************************************

	// **********************************************************************
	// Create, Update and Delete Group
	// **********************************************************************
	@POST
	@Path("/policy/{userId}/{appName}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response createPolicy(@PathParam("userId") String userId, @PathParam("appName") String appName, VoPolicy policy) throws TapirException {
		try {
			System.out.println("createPolicy -----> userId=" + userId + ", name=" + appName + ", policy=" + policy);
			List<VoRule> rList = policy.getRuleList();
			for (VoRule rule : rList) {
				System.out.println("createPolicy -----> rule=" + rule);
			}

			VoPolicy result = policy; // PAPService.getInstance().createApp(app);
			return Response.status(Status.OK).entity(result).type(MediaType.APPLICATION_JSON).build();
		} catch (Throwable t) {
			return createErrorResponse(t);
		}
	}

	public VoPolicy updatePolicy(VoPolicy policy) throws TapirException {
		return null;
	}

	public void deletePolicy(long policyId) throws TapirException {
	}

	// **********************************************************************
	// Get Policy
	// **********************************************************************
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

	// Get all policies for a particular user and groups he/she belong to
	public List<VoPolicy> getAllPoliciesByUserWithGroup(String userId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	public List<VoPolicy> getAllPoliciesByUserWithGroupAndAction(String userId, String action, Boolean fLoadRules) throws TapirException {
		return null;
	}

	// **********************************************************************
	// Policy and User Relationship
	// **********************************************************************

	// Get all policies for an user
	public List<VoPolicy> getPoliciesByUser(String userId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	public List<VoPolicy> getPoliciesByUserAndAction(String userId, String action, Boolean fLoadRules) throws TapirException {
		return null;
	}

	// **********************************************************************
	// Policy and Group Relationship
	// **********************************************************************

	// Get all policies for a group
	public List<VoPolicy> getPoliciesByGroup(long groupId, Boolean fLoadRules) throws TapirException {
		return null;
	}

	public List<VoPolicy> getPoliciesByGroupAndAction(long groupId, String action, Boolean fLoadRules) throws TapirException {
		return null;
	}

	// **********************************************************************
	// R U L E
	// **********************************************************************

	// **********************************************************************
	// Create, Update and Delete Rule
	// **********************************************************************

	public VoRule createRule(long policyId, VoRule rule) throws TapirException {
		return null;
	}

	public VoRule updateRule(long policyId, VoRule rule) throws TapirException {
		return null;
	}

	public void deleteRule(long ruleId) throws TapirException {
	}

	// **********************************************************************
	// Get Rule
	// **********************************************************************

	public VoRule getRuleById(String ruleId) throws TapirException {
		return null;
	}

	// **********************************************************************
	// Policy and Rule Relationship
	// **********************************************************************
	public List<VoRule> getRulesByPolicy(long policyId) throws TapirException {
		return null;
	}

	// used after createRule when policyId is not created yet
	public void bindRuleByPolicy(long policyId, long ruleId) throws TapirException {
	}

}
