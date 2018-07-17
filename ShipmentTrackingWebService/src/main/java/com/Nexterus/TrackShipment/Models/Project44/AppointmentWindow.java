package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class AppointmentWindow {

	private String startDateTime;
	private String endDateTime;
	
	private String localizedTimeZoneIdentifier;

	public AppointmentWindow() {
		super();
	}

	public AppointmentWindow(String startDateTime, String endDateTime, String localizedTimeZoneIdentifier) {
		super();
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
		this.localizedTimeZoneIdentifier = localizedTimeZoneIdentifier;
	}

	@JsonProperty("startDateTime")
	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	@JsonProperty("endDateTime")
	public String getEndDateTime() {
		return endDateTime;
	}

	public void setEndDateTime(String endDateTime) {
		this.endDateTime = endDateTime;
	}

	/*@JsonProperty("localizedTimeZoneIdentifier")*/
	@JsonIgnore
	public String getLocalizedTimeZoneIdentifier() {
		return localizedTimeZoneIdentifier;
	}

	public void setLocalizedTimeZoneIdentifier(String localizedTimeZoneIdentifier) {
		this.localizedTimeZoneIdentifier = localizedTimeZoneIdentifier;
	}

}
