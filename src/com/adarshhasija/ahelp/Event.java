package com.adarshhasija.ahelp;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseObject;


public class Event {
	
	private Exam exam;
	private List<Action> actions = new ArrayList<Action>();  //Newest first
	

	public Exam getExam() {
		return exam;
	}


	public void setExam(Exam exam) {
		this.exam = exam;
	}


	public List<Action> getActions() {
		return actions;
	}


	public void setActions(List<Action> actions) {
		this.actions = actions;
	} 
	
	public Action getLatestAction() {
		return this.actions.get(0);
	}
	
}
