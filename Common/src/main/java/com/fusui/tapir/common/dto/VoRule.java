package com.fusui.tapir.common.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VoRule {

	private Long sid;
	private String name;
	private String description;
	private Date birthday;
	
	private Date version;
	
	// run-time form based on serialized form in db
	private List <String> criteriaList;
	private transient VoExpNode expRootNode;
	private transient List<VoExpNode> expNodeList;
	
	public Long getSid() {
		return sid;
	}

	public void setSid(Long sid) {
		this.sid = sid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public Date getVersion() {
		return version;
	}

	public void setVersion(Date version) {
		this.version = version;
	}

	public void setCriteriaList(List<String> criteriaList) {
		this.criteriaList = criteriaList;
	}

	public List <String> getCriteriaList() {
		return this.criteriaList;
	}
	
	public VoExpNode getExpRootNode() {
		return expRootNode;
	}

	public void setExpRootNode(VoExpNode expRootNode) {
		this.expRootNode = expRootNode;
	}
	
	public List<VoExpNode> getExpNodeList() {
		return expNodeList;
	}

	public void setExpNodeList(List<VoExpNode> expNodeList) {
		this.expNodeList = expNodeList;
	}
	

	@Override
	public String toString() {
		return "sid="+sid+", name="+name;
	}
	

}
