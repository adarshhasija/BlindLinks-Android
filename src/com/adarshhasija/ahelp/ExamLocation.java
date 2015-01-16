package com.adarshhasija.ahelp;

import java.util.UUID;

/*
 * This class is intended to be a copy of the location specifically for the exam
 * If the original location record gets deleted, the exam still has its own copy of the location details
 * ExamLocation is an extension with Location with the addition of a pointer to the original location object
 * 
 */
public class ExamLocation extends Location {
	
	private Location location;

	public ExamLocation(String parseId, String uuid) {
		super(parseId, uuid);
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
	

}
