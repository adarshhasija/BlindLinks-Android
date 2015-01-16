package com.adarshhasija.ahelp;

import java.util.UUID;

public class Location {
	
	private String parseId;
	private String uuid;
	private String title;
	
	
	public Location(String parseId, String uuid) {
		super();
		this.parseId = parseId;
		this.uuid = uuid;
	}


	public String getParseId() {
		return parseId;
	}


	public void setParseId(String parseId) {
		this.parseId = parseId;
	}


	public String getUuid() {
		return uuid;
	}


	public void setUuid(String uuid) {
		this.uuid = uuid;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	
	

}
