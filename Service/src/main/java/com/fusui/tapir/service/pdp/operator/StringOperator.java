package com.fusui.tapir.service.pdp.operator;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.FoundryConstants;

public class StringOperator implements IOperator {
	private static final Logger logger = LoggerFactory.getLogger(StringOperator.class);

	public StringOperator() {
	}
	
	public Boolean Equal(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}

		if (s1 == null && s2 != null) {
			return false;
		}

		if (s1 != null && s2 == null) {
			return false;
		}

		return s1.equals(s2);
	}
	
	public Boolean NotEqual(String s1, String s2) {
		return ! Equal (s1, s2);
	}

	// case sensitive
	public Boolean Contain(String s1, String s2) {
		if (s1 == null && s2 == null) {
			return true;
		}

		if (s1 == null && s2 != null) {
			return false;
		}

		if (s1 != null && s2 == null) {
			return false;
		}

		return s1.contains(s2);
	}
	
	public Boolean NotContain(String s1, String s2) {
		return !Contain(s1,s2);
	}

	// case sensitive
	public Boolean In(String s1, String s2) {
		String[] s1Array = FoundryUtil.split(s1, FoundryConstants.VALUE_DELIMITER);
		Set <String> aSet = new HashSet<String>();
		for (String s: s1Array) {
			aSet.add(s);
		}
		
		String[] s2Array = FoundryUtil.split(s2, FoundryConstants.VALUE_DELIMITER);
		for (String s: s2Array) {
			if (aSet.contains(s) ) {
				return true;
			}
		}
		return false;
	}
	
	public Boolean NotIn(String s1, String s2) {
		return !In(s1,s2);
	}
	
	
//	// case sensitive
//	public Boolean In(String s1, String s2) {
//		if (s1 == null && s2 == null) {
//			return true;
//		}
//
//		if (s1 == null && s2 != null) {
//			return false;
//		}
//
//		if (s1 != null && s2 == null) {
//			return false;
//		}
//
//		return s2.contains(s1);
//	}
//	
//	public Boolean NotIn(String s1, String s2) {
//		return !In(s1,s2);
//	}

	
	
	public Boolean IsEmpty(String s1) {
		return s1 == null;
	}

	public Boolean IsAnything(String s1) {
		return s1 != null;
	}

	
//	public Boolean EqualIgnoreCase(String s1, String s2) {
//		if (s1 == null && s2 == null) {
//			return true;
//		}
//
//		if (s1 == null && s2 != null) {
//			return false;
//		}
//
//		if (s1 != null && s2 == null) {
//			return false;
//		}
//
//		return s1.equalsIgnoreCase(s2);
//	}
//
//	public Boolean ContainIgnoreCase(String s1, String s2) {
//		
//		if (s1 == null && s2 == null) {
//			return true;
//		}
//
//		if (s1 == null && s2 != null) {
//			return false;
//		}
//
//		if (s1 != null && s2 == null) {
//			return false;
//		}
//		
//		return s1.toLowerCase().contains(s2.toLowerCase());
//	}
//
//	public Boolean InIgnoreCase(String s1, String s2) {
//		if (s1 == null && s2 == null) {
//			return true;
//		}
//
//		if (s1 == null && s2 != null) {
//			return false;
//		}
//
//		if (s1 != null && s2 == null) {
//			return false;
//		}
//
//		return s2.toLowerCase().contains(s1.toLowerCase());
//	}

}
