package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ArrivalEstimate {

	@Autowired
	private AppointmentWindow estimatedArrivalWindow;
	private String lastCalculatedDateTime;
	public ArrivalEstimate() {
		super();
		// TODO Auto-generated constructor stub
	}
	@JsonProperty("estimatedArrivalWindow")
	public AppointmentWindow getEstimatedArrivalWindow() {
		return estimatedArrivalWindow;
	}
	public void setEstimatedArrivalWindow(AppointmentWindow estimatedArrivalWindow) {
		this.estimatedArrivalWindow = estimatedArrivalWindow;
	}
	@JsonProperty("lastCalculatedDateTime")
	public String getLastCalculatedDateTime() {
		return lastCalculatedDateTime;
	}
	public void setLastCalculatedDateTime(String lastCalculatedDateTime) {
		this.lastCalculatedDateTime = lastCalculatedDateTime;
	}
	
	
}
