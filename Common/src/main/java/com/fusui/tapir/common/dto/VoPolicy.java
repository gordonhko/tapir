package com.fusui.tapir.common.dto;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class VoPolicy {
	
	private Long mid;
	private String type;
	private String action;
	private Date birthday;
	
	private Long sid;
	private String name;
	private String description;
	private Integer status;
	private Date version;
	
	private List <VoRule> ruleList;
	
	
	public Long getMid() {
		return mid;
	}
	public void setMid(Long mid) {
		this.mid = mid;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
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
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Date getVersion() {
		return version;
	}
	public void setVersion(Date version) {
		this.version = version;
	}
	
	
	public List<VoRule> getRuleList() {
		return ruleList;
	}
	public void setRuleList(List<VoRule> ruleList) {
		this.ruleList = ruleList;
	}
	public void setNotEvalApplicable(boolean b) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String toString() {
		return "mid="+mid+", name="+name+", action="+action;
	}
	
	
}

