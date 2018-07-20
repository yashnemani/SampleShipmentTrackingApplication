package com.Nexterus.TrackShipment.Models;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Component
public class Status {

	private String statusDate;
	private String statusCode;
	private String statusCity;
	private String statusState;

	public Status() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusCity(String statusCity) {
		this.statusCity = statusCity;
	}

	public void setStatusState(String statusState) {
		this.statusState = statusState;
	}

	@JsonProperty("statusDate")
	public String getStatusDate() {
		return statusDate;
	}

	@JsonProperty("statusCode")
	public String getStatusCode() {
		return statusCode;
	}

	@JsonProperty("statusCity")
	public String getCity() {
		return statusCity;
	}

	@JsonProperty("statusState")
	public String getState() {
		return statusState;
	}
}
