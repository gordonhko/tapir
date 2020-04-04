package com.fusui.tapir.common.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.fusui.tapir.common.FoundryUtil;
import com.fusui.tapir.common.dto.VoMetaOperator.VoOperatorMetaList;

public class VoCriteria {

	private String attrName;
	private String attrValue;
	private String attrType;
	private String operator;

	public VoCriteria() {
		
	}
	
	public VoCriteria(String strCriteria) {
		List <String> tokens = FoundryUtil.tokenize(strCriteria, FoundryConstants.DELIMITER_CRITERIA_OP);
		this.attrName = tokens.get(0);
		this.attrValue = tokens.get(1);
		this.operator = tokens.get(2);
		this.attrType = tokens.get(3);
	}

	@Override
	public VoCriteria clone() {
		VoCriteria copy = new VoCriteria();
		copy.attrName = this.attrName;
		copy.attrValue = this.attrValue;
		copy.attrType = this.attrType;
		copy.operator = this.operator;
		return copy;
	}

	public List<String> getAttrOperList(VoDictionary dict) {
		List<String> list = new ArrayList<String>();
		Map<String, VoOperatorMetaList> map = dict.getOperMap();

		Map<String, VoMetaOperand> attrMap = FoundryUtil.buildAttributeMetaMap(dict.getAttrList());
		VoMetaOperand am = attrMap.get(attrName);
		String attriType = am.getValueType();
		String attriSelectionType = am.getSelectionType();

		VoOperatorMetaList mlist = map.get(attriType + "." + attriSelectionType);
		List<VoMetaOperator> omlist = mlist.getList();
		for (VoMetaOperator om : omlist) {
			list.add(om.getOperatorUIName());
		}

		Collections.sort(list);
		return list;
	}

	public List<String> getAttrValueList(VoDictionary dict) {
		List<String> list = new ArrayList<String>();
		List<VoMetaOperand> alist = dict.getAttrList();
		for (VoMetaOperand am : alist) {
			if (am.getAttrName().equals(attrName)) {
				String pvs = am.getPossibleValues();
				if (pvs == null)
					break;
				StringTokenizer st = new StringTokenizer(pvs, ",");
				while (st.hasMoreTokens()) {
					list.add(st.nextToken());
				}
				break;
			}
		}

		if (list.size() == 0 && attrValue != null) {
			list.add(attrValue);
		}

		Collections.sort(list);
		return list;
	}

	

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getAttrValue() {
		return attrValue;
	}

	public void setAttrValue(String attrValue) {
		this.attrValue = attrValue;
	}

	public String getAttrType() {
		return attrType;
	}

	public void setAttrType(String attrType) {
		this.attrType = attrType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

		@Override
	public String toString() {
		return "Name=" + attrName + " Value=" + attrValue + " Type=" + attrType + " Operator=" + operator + "|";
	}

}
