package com.adarshhasija.ahelp;

import java.util.UUID;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


@ParseClassName("Request")
public class Request extends ParseObject {

	public ParseUser getSender() {
        return getParseUser("sender");
    }
     
    public void setSender(ParseUser currentUser) {
        put("sender", currentUser);
    }
    
    public ParseUser getRecipient() {
        return getParseUser("recipient");
    }
     
    public void setRecipient(ParseUser currentUser) {
        put("recipient", currentUser);
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
     
    public static ParseQuery<Request> getQuery() {
        return ParseQuery.getQuery(Request.class);
    }
}
