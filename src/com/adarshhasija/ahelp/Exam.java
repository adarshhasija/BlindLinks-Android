package com.adarshhasija.ahelp;

import java.util.UUID;

public class Exam {

	private String parseId;
	private String uuid;
	private long dateTimeMillis;
	private Location location;
	private String subject;
	private String notes;
	
	public Exam(String parseId, String uuid) {
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

	public long getDateTimeMillis() {
		return dateTimeMillis;
	}

	public void setDateTimeMillis(long dateTimeMillis) {
		this.dateTimeMillis = dateTimeMillis;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
}
