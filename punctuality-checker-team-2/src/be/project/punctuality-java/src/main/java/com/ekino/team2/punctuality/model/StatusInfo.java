package com.ekino.team2.punctuality.model;

public enum StatusInfo {
	A0("On-time morning."),A("Late after 9h:15 AM."), AA("Absent cause late after 10:00 AM."), AB("Morning absent unplanned."),
	P0("On-time affternoon."),P("Late after 14h:15 PM."), PA("Absent cause late after  15:00 PM."), PB("Affternoon absent unplanned."),
	FF("The working day complete.");

	private final String description;

	StatusInfo(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}
