package com.adarshhasija.ahelp;

import java.util.Date;
import java.util.UUID;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Event")
public class Event extends ParseObject {
	
	public Date getDateTime() {
		return getDate("dateTime");
	}
	
	public void setDateTime(Date dateTime) {
		put("dateTime", dateTime);
	}

	public ParseUser getCreator() {
        return getParseUser("creator");
    }
     
    public void setCreator(ParseUser currentUser) {
        put("creator", currentUser);
    }
     
    public boolean isDraft() {
        return getBoolean("isDraft");
    }
     
    public void setDraft(boolean isDraft) {
        put("isDraft", isDraft);
    }
     
    public void setUuidString() {
        UUID uuid = UUID.randomUUID();
        put("uuid", uuid.toString());
    }
     
    public String getUuidString() {
        return getString("uuid");
    }
     
    public static ParseQuery<Event> getQuery() {
        return ParseQuery.getQuery(Event.class);
    }
}
