package com.fusui.tapir.common.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fusui.tapir.common.dto.VoMetaOperator.VoOperatorMetaList;

public class VoDictionary {

	private List<VoMetaOperand> attrList;

	private Map<String, VoOperatorMetaList> operMap;

	// used for PDP only
	private int maxOperatorLen;

	public VoDictionary() {
	}

	public VoDictionary(List<VoMetaOperand> validAttrList, HashMap<String, VoOperatorMetaList> opMap, int maxOperatorLen) {
		this.attrList = validAttrList;
		this.operMap = opMap;
		this.maxOperatorLen = maxOperatorLen;
	}

	public List<VoMetaOperand> getAttrList() {
		return attrList;
	}

	public void setAttrList(List<VoMetaOperand> attrList) {
		this.attrList = attrList;
	}

	public Map<String, VoOperatorMetaList> getOperMap() {
		return operMap;
	}

	public void setOperMap(Map<String, VoOperatorMetaList> operMap) {
		this.operMap = operMap;
	}

	public int getMaxOperatorLen() {
		return maxOperatorLen;
	}

	public void setMaxOperatorLen(int maxOperatorLen) {
		this.maxOperatorLen = maxOperatorLen;
	}

}
