package com.fusui.tapir.common.dto;

public class VoMetaOperand {

	private String attrName;
	private String valueType; // String, Integer ...
	private String selectionType; // Text Field, Single Selection
	private String possibleValues; // fill in at run-time, no need to persistent

	private String attrUIName;
	private String attrDBName;

	private String operKey; // use this key to get all possible operator from OperMetadata Table

	public String getAttrDBName() {
		return attrDBName;
	}

	public void setAttrDBName(String dbColumnName) {
		this.attrDBName = dbColumnName;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getPossibleValues() {
		return possibleValues;
	}

	public void setPossibleValues(String possibleValues) {
		this.possibleValues = possibleValues;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	public String getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}

	public String getOperKey() {
		return operKey;
	}

	public void setOperKey(String key) {
		this.operKey = key;
	}

	public String getAttrUIName() {
		return attrUIName;
	}

	public void setAttrUIName(String uiName) {
		this.attrUIName = uiName;
	}

	@Override
	public String toString() {
		return "name=" + getAttrName() + " value=" + getPossibleValues() + " type=" + getValueType() + " selection=" + getSelectionType() + " dbColumn=" + getAttrDBName() + " operationsList="
				+ getOperKey() + "[" + getPossibleValues() + "]";
	}
}
