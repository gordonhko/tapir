package com.fusui.tapir.common.dto;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VoApp {
	

	private Long mid;
	private Long parentId;

	private Integer maxCopy;
	private String type;
	private Date birthday;
	private Date deathday;
	
	// version
	private Long sid;
	private String name;
	private String desc;
	private Long owner;
	private Integer status;
	private Date version;
	

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
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public Integer getMaxCopy() {
		return maxCopy;
	}
	public void setMaxCopy(Integer maxCopy) {
		this.maxCopy = maxCopy;
	}

	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public Date getDeathday() {
		return deathday;
	}
	public void setDeathday(Date deathday) {
		this.deathday = deathday;
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
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

	public Long getOwner() {
		return this.owner;
	}
	public void setOwner(Long owner) {
		this.owner = owner;
	}


	@Override
	public String toString() {
		return "mid="+mid+", parentId="+parentId+", name="+name+", maxCopy="+maxCopy;
	}
}
