package com.adarshhasija.ahelp;

import java.util.UUID;

import com.parse.ParseUser;

public class Action {

	private String parseId;
	private String uuid;
	private ParseUser from;
	private ParseUser to;
	private String type;
	
	
	
	public Action(String parseId, String uuid, ParseUser from, ParseUser to) {
		super();
		this.parseId = parseId;
		this.uuid = uuid;
		this.from = from;
		this.to = to;
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

	public ParseUser getFrom() {
		return from;
	}

	public void setFrom(ParseUser from) {
		this.from = from;
	}

	public ParseUser getTo() {
		return to;
	}

	public void setTo(ParseUser to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	
	
}
