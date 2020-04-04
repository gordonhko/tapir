//http://www.hascode.com/2013/12/jax-rs-2-0-rest-client-features-by-example/
//http://www.codingpedia.org/ama/restful-web-services-example-in-java-with-jersey-spring-and-mybatis/
//http://stackoverflow.com/questions/27806529/using-default-providers-messagebodywriters-in-jersey-2

package com.fusui.tapir.client.rs;

import java.util.HashMap;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.annotation.JacksonFeatures;
import com.fusui.tapir.common.FoundryProperties;
import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.client.IFoundryClient;
import com.fusui.tapir.common.dto.FoundryConstants;
import com.fusui.tapir.common.dto.VoTable;


/**
 * Description:
 * Copyright:    Copyright (c) 2015
 * @author Gordon Ko
 * @version 1.0
 */

// http://www.hascode.com/2013/12/jax-rs-2-0-rest-client-features-by-example/
// http://docs.orFoundryUtile.com/middleware/1213/wls/RESTF/develop-restful-client.htm#RESTF155


public class FoundryWsClient implements IFoundryClient {
	
//	static {
//		System.setProperty("javax.net.debug",  "all");
//	}

	
	private Client rsClient;
	private WebTarget target;
	private ObjectMapper mapper = new ObjectMapper();

	public FoundryWsClient(String baseURI) {
    	//this.baseURI = baseURI;
        this.rsClient = ClientBuilder.newClient().register(JacksonFeatures.class);
        this.target = rsClient.target(baseURI);
	}

    public FoundryWsClient(String baseURI, FoundryProperties properties) {
	    //this.rsClient = JerseyClientFactory.newFactory().withProperties(properties).create();
        this.rsClient = ClientBuilder.newClient().register(JacksonFeatures.class);
        this.target = rsClient.target(baseURI);
    }

    public FoundryWsClient(String baseURI, Client rsClient) {
        this.rsClient = rsClient;
        this.target = rsClient.target(baseURI);
    }

//	public String getBaseURI() {
//		return this.baseURI;
//	}
	
	
//	@GET
//	@Path("/SYSTEM/health")
//	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String checkHealth() throws TapirException {
		try {
			WebTarget resourceTarget = target.path(FoundryConstants.RESOURCE_ROOT).path(FoundryConstants.RESOURCE_SYSTEM_HEALTH);
			return resourceTarget.request().get(String.class);
		}
		catch (Throwable t) {
			throw new TapirException(t);
		}

	}

//	@GET
//	@Path("/version")
//	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getVersion() throws TapirException {
		try {
			WebTarget resourceTarget = target.path(FoundryConstants.RESOURCE_ROOT).path(FoundryConstants.RESOURCE_VERSION);
	        return resourceTarget.request(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (Throwable t) {
	        throw new TapirException(t);
		}
	}
	
//	@GET
//	@Path("/properties")
//	@Produces(MediaType.APPLICATION_JSON)
	@Override
	public String getProperties() throws TapirException {
		try {
			WebTarget resourceTarget = target.path(FoundryConstants.RESOURCE_ROOT).path(FoundryConstants.RESOURCE_PROPERTIES);
			return resourceTarget.request(MediaType.APPLICATION_JSON).get(String.class);
		}
		catch (Throwable t) {
			throw new TapirException(t);
		}
	}
	

	@Override
	public VoTable getTable(Long userId, Long workspaceId, Long handleId, String sqlId, String sqlStmt, HashMap<String, String> varMap) throws TapirException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VoTable getMockupTable(Long userId, Long workspaceId, Long handleId, String sqlId, String sqlStmt, HashMap<String, String> varMap) throws TapirException {
		
		try {
			
			WebTarget resourceTarget = target.path(FoundryConstants.RESOURCE_ROOT)
											 .path(FoundryConstants.RESOURCE_FOUNDRY)
											 .path(Long.toString(userId))
											 .path(Long.toString(workspaceId))
											 .path(Long.toString(handleId))
											 .path(sqlId);
											 //.queryParam(FoundryConstants.QUERY_PARAM_SQL_STMT, sqlStmt)
											 //.queryParam(FoundryConstants.QUERY_PARAM_VAR_MAP, seralizedMap);
											 
			varMap.put("$SQL", sqlStmt);
			
	        return  resourceTarget.request(MediaType.APPLICATION_JSON)
	        		.post(Entity.entity(varMap, MediaType.APPLICATION_JSON), VoTable.class);
			
		}
		catch (Throwable t) {
			throw new TapirException(t);
		}
	
	}

	@Override
	public String getVersion(String s) throws TapirException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
//	@GET
//	@Path("/definitions/{userId}")
//	@Produces(MediaType.APPLICATION_JSON)
//	@Override
//	public List<WflVoDefinition> getAllDefinitions(String userName, boolean allVersions) throws FoundryException {
//		WebResource resource = rsClient.resource(baseURI) 
//        		.path(Constants.RESOURCE_ROOT)
//				.path(ArmsConstants.RESOURCE_DEFINITIONS)
//				.path(userName)
//				.queryParam(ArmsConstants.QUERY_PARAM_ALL_VERSIONS, Boolean.toString(allVersions) );
//        
//        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON).get(ClientResponse.class);
//        
//        if (response.getStatus() == Status.OK.getStatusCode()) {
//        	if (response.hasEntity()) {
//        		return response.getEntity(WflVoDefinitions.class).getDefList();
//        	}
//        	return null;
// 		}
//        throw new ArmsException(response.getEntity(ArmsRsErrorMessage.class).toString());
//		return null;
        
//	}
	

}


