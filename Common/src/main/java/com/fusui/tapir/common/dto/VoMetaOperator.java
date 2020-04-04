package com.fusui.tapir.common.dto;

import java.util.List;

public class VoMetaOperator {

	public static class VoOperatorMetaList {
		private List<VoMetaOperator> list;

		public VoOperatorMetaList() {
		}

		public List<VoMetaOperator> getList() {
			return list;
		}

		public void setList(List<VoMetaOperator> list) {
			this.list = list;
		}
	}

	private String valueType;
	private String selectionType;

	private String operatorName;
	private String className;
	private String methodName;
	private int numOfOperands;

	private String operatorUIName;
	private String operatorDBName;

	private String criteriaEnable;

	public String getOperatorDBName() {
		return operatorDBName;
	}

	public void setOperatorDBName(String operClauseName) {
		this.operatorDBName = operClauseName;
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

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public int getNumOfOperands() {
		return numOfOperands;
	}

	public void setNumOfOperands(int numOfOperands) {
		this.numOfOperands = numOfOperands;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String operatorClass) {
		this.className = operatorClass;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setOperatorUIName(String uiName) {
		this.operatorUIName = uiName;
	}

	public String getOperatorUIName() {
		return operatorUIName;
	}

	public void setCriteriaEnable(String enable) {
		this.criteriaEnable = enable;
	}

	public String getCriteriaEnable() {
		return this.criteriaEnable;
	}

	@Override
	public String toString() {
		// return "opName="+getOperatorName() +" # of opds="+getNumOfOperands()+
		// " className="+getClassName()+" methodName="+getMethodName()+" returnType="+getReturnedType()+" dbOperator="+getDbOperName();
		return "opName=" + getOperatorName() + " numOfOperands=" + getNumOfOperands() + " className=" + getClassName() + " methodName=" + getMethodName() + " dbOperator=" + getOperatorDBName();

	}
}
