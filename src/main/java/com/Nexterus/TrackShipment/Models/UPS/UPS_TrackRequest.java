package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class UPS_TrackRequest {

	@Autowired
	private UPS_Security security;
	
	@Autowired
	private TrackRequest trackRequest;

	public UPS_TrackRequest() {
	}


	@JsonProperty("UPSSecurity")
	public UPS_Security getSecurity() {
		return security;
	}

	@JsonProperty("TrackRequest")
	public TrackRequest getTrackRequest() {
		return trackRequest;
	}

	public void setTrackRequest(TrackRequest trackRequest) {
		this.trackRequest = trackRequest;
	}
	
	
}
