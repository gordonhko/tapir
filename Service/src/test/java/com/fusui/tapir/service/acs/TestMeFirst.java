package com.fusui.tapir.service.acs;

import java.sql.SQLException;
import java.util.List;

import org.junit.Ignore;

import com.fusui.tapir.common.TapirException;
import com.fusui.tapir.service.dal.TransactionManager;
import com.fusui.tapir.service.dao.DaoGroup;
import com.fusui.tapir.service.dao.RowGroup;
import com.fusui.tapir.service.dao.SqlDictionary;

@Ignore
public class TestMeFirst {


	private void doTask() throws Exception {
		SqlDictionary.getInstance().initSql();
		createGroups();
	}

	private void createGroups() throws TapirException {
		
		
		try {
			TransactionManager.transactionStart();
			DaoGroup.getInstance().createGroup(111L, 222L, "Me262", "German", true);
			DaoGroup.getInstance().createGroup(111L, 222L, "Me109", "German", true);
			
			
			List<RowGroup> rowList = DaoGroup.getInstance().readGroup(111L, 222L);
			for (RowGroup row : rowList) {
				System.out.println(row);
			}
			
			
			TransactionManager.transactionCommit();
			
		} catch (Throwable t) {
			t.printStackTrace();
			TransactionManager.transactionRollback();
			throw new TapirException(t);
		}
		
//		try {
//			TransactionManager.transactionStart();
//		
//		} catch (Throwable t) {
//			t.printStackTrace();
//			TransactionManager.transactionRollback();
//			throw new TapirException(t);
//		}
		
		
	}

	public static void main(String[] args) throws Exception {
		TestMeFirst cliTest = new TestMeFirst();
		cliTest.doTask();
		System.out.println("Test OK");
		System.exit(0);
	}

}
