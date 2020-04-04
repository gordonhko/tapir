package com.fusui.tapir.client.rs;

import com.fusui.tapir.client.rs.FoundryWsClient;



public class TestBase {

	private static String URL = "http://localhost:8080/foundry";
	private static FoundryWsClient client;
	
	public static void setUpBeforeClass() throws Exception {
		client = new FoundryWsClient(URL);
	}
	
	protected FoundryWsClient getClient(){
		return client;
	}
}
