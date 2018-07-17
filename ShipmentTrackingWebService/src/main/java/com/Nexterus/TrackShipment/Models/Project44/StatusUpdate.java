package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class StatusUpdate {

	private String timestamp;
	private String statusCode;
	private int stopNumber;
	@Autowired
	private StatusReason statusReason;
	@Autowired
	private Address44 address;
	@Autowired
	private GeoCoordinates geoCoordinates;

	public StatusUpdate() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("timestamp")
	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("statusCode")
	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	@JsonProperty("stopNumber")
	public int getStopNumber() {
		return stopNumber;
	}

	public void setStopNumber(int stopNumber) {
		this.stopNumber = stopNumber;
	}

	@JsonProperty("statusReason")
	public StatusReason getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(StatusReason statusReason) {
		this.statusReason = statusReason;
	}

	@JsonProperty("address")
	public Address44 getAddress() {
		return address;
	}

	public void setAddress(Address44 address) {
		this.address = address;
	}

	@JsonProperty("geoCoordinates")
	public GeoCoordinates getGeoCoordinates() {
		return geoCoordinates;
	}

	public void setGeoCoordinates(GeoCoordinates geoCoordinates) {
		this.geoCoordinates = geoCoordinates;
	}

}
