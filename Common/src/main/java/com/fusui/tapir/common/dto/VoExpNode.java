package com.fusui.tapir.common.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

public class VoExpNode {

	private String nodeId;
	private String parentId;
	private String nodeType;
	private String value;
	private String valueType;

	// no need to marshall back to client to avoid recursive and duplicated data
	// @XmlTransient
	private List<VoExpNode> childList;

	// private String methodName;

	@Override
	public VoExpNode clone() {
		return clone(false);
	}

	// the cloneChild only need to be true if it is processed in memory
	// marshalling and marshalling is no necessary to set to true
	public VoExpNode clone(boolean cloneChild) {
		VoExpNode copy = new VoExpNode();
		copy.nodeId = this.nodeId;
		copy.parentId = this.parentId;
		copy.nodeType = this.nodeType;
		copy.value = this.value;
		copy.valueType = this.valueType;

		if (cloneChild) {
			if (this.childList != null) {
				copy.childList = new ArrayList<VoExpNode>(this.childList.size());
				for (VoExpNode node : childList) {
					copy.childList.add(node.clone(false));
				}
			}
		}
		return copy;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueType() {
		return valueType;
	}

	public void setValueType(String valueType) {
		this.valueType = valueType;
	}

	// no need to marshall back to client to avoid recursive and duplicated data
	@XmlTransient
	public List<VoExpNode> getChildList() {
		if (childList == null) {
			childList = new ArrayList<VoExpNode>();
		}
		return childList;
	}

	public void setChildList(List<VoExpNode> childList) {
		this.childList = childList;
	}

	public String getId() {
		return nodeId;
	}

	public void setId(String id) {
		this.nodeId = id;

	}

	@Override
	public String toString() {
		return "id=" + this.nodeId + " value=" + this.value + " valueType="+valueType+" nodeType=" + this.nodeType+"|";

	}

}
