package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class StopStatus {

	private int stopNumber;
	private String statusCode;
	private String arrivalCode;
	private String arrivalDateTime;
	private String departureDateTime;

	public StopStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("stopNumber")
	public int getStopNumber() {
		return stopNumber;
	}

	public void setStopNumber(int stopNumber) {
		this.stopNumber = stopNumber;
	}

	@JsonProperty("statusCode")
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@JsonProperty("arrivalCode")
	public String getArrivalCode() {
		return arrivalCode;
	}

	public void setArrivalCode(String arrivalCode) {
		this.arrivalCode = arrivalCode;
	}

	@JsonProperty("arrivalDateTime")
	public String getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(String arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	@JsonProperty("departureDateTime")
	public String getDepartureDateTime() {
		return departureDateTime;
	}

	public void setDepartureDateTime(String departureDateTime) {
		this.departureDateTime = departureDateTime;
	}
}
