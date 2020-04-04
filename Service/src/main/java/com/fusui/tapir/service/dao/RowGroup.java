package com.fusui.tapir.service.dao;

import java.util.Date;

public class RowGroup extends RowBase {

	// SELECT GROUP_ID, GROUP_MASTER_ID, CREATOR, CREATION_DATE, DELETED, NAME, DESCRIPTION, ENABLED WHERE COMPANY = ?
	private Long id;
	private Long masterId;
	//private Long companyId;
	private Long creator;
	private Date timeStamp;
	private Boolean deleted;
	private String name;
	private String description;
	private Boolean enabled;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMasterId() {
		return masterId;
	}
	public void setMasterId(Long masterId) {
		this.masterId = masterId;
	}
//	public Long getCompany() {
//		return masterId;
//	}
//	public void setCompanyId(Long companyId) {
//		this.companyId = companyId;
//	}
	public Long getCreator() {
		return creator;
	}
	public void setCreator(Long creator) {
		this.creator = creator;
	}
	public Date getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Date timeStamp) {
		this.timeStamp = timeStamp;
	}
	public Boolean getDeleted() {
		return deleted;
	}
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
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
	public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	@Override
	public String toString() {
		//return String.format("%d, %d" , id, masterId);
		
		return String.format("%d, %d, %d, %s, %b, %s, %s, %b", id, masterId, creator, timeStamp.toString(), deleted, name, description, enabled);
	}

}
