package com.fusui.tapir.common.client;

import java.util.HashMap;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.dto.VoTable;

public interface IFoundryClient {

	/***********************************************************************************
	 *
	 * Common 
	 * 
	 ***********************************************************************************/
	//	@GET
	//	@Path("/SYSTEM/health")
	//	@Produces(MediaType.APPLICATION_JSON)
	public String checkHealth() throws TapirException;

	//	@GET
	//	@Path("/version")
	//	@Produces(MediaType.APPLICATION_JSON)
	public String getVersion(String s) throws TapirException;

	
	//	@GET
	//	@Path("/properties")
	//	@Produces(MediaType.APPLICATION_JSON)
	public String getProperties() throws TapirException;

	
	public VoTable getTable( Long userId, Long workspaceId, Long handleId, String sqlId, String sqlStmt,  HashMap <String, String> varMap) throws TapirException;

	public VoTable getMockupTable( Long userId, Long workspaceId, Long handleId, String sqlId, String sqlStmt,  HashMap <String, String> varMap) throws TapirException;

	String getVersion() throws TapirException;

}
