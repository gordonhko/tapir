package com.fusui.tapir.client.rs;

import java.util.HashMap;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.common.client.IFoundryClient;
import com.fusui.tapir.common.dto.VoTable;

public class TestRestful extends TestBase {

	private void test1() throws TapirException {
		IFoundryClient client = getClient();

		System.out.println("status="+client.checkHealth());
		System.out.println("version="+client.getVersion());
		System.out.println("properties="+client.getProperties());
		System.out.println();
	}
	
	private void test2() throws TapirException {

		IFoundryClient client = getClient();

		HashMap<String, String> varMap = new HashMap<String, String>();
		varMap.put("key1", "value1");
		varMap.put("key2", "value2");
		varMap.put("key3", "value3");

		VoTable table = client.getMockupTable(1L, 2L, 3L, "SqlId", "Select 1 from dual", varMap);

		System.out.println(table);
	}

	public static void main(String[] args) {

		System.out.println("TestRestful starting ...\n");

		try {
			
			setUpBeforeClass();
			
			TestRestful client = new TestRestful();
			client.test1();
			client.test2();

		} catch (Throwable t) {
			t.printStackTrace();
		}

		System.out.println("\nTestRestful done");
		System.exit(0);
	}

}