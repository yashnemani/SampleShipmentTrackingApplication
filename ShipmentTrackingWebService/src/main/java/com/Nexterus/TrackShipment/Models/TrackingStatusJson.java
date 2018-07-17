package com.Nexterus.TrackShipment.Models;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TrackingStatusJson {

	private String user = "TrackingWebService";
	private String password = "TestPass";
	private String scac;
	private List<Tracking> trackingInfo;
	private String shipType = "LTL";

	public void setScac(String scac) {
		this.scac = scac;
	}

	@JsonProperty("trackingInfo")
	public List<Tracking> getTrackingInfo() {
		return trackingInfo;
	}

	public void setTrackingInfo(List<Tracking> trackingInfo) {
		this.trackingInfo = trackingInfo;
	}

	public TrackingStatusJson() {
		super();
	}

	@JsonProperty("user")
	public String getUser() {
		return user;
	}

	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	@JsonProperty("scac")
	public String getCarrierCode() {
		return scac;
	}

	@JsonProperty("shipType")
	public String getShipType() {
		return shipType;
	}
}
