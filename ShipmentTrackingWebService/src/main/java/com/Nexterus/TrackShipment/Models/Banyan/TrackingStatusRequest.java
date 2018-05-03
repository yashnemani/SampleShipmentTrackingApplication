package com.Nexterus.TrackShipment.Models.Banyan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TrackingStatusRequest {

	@Autowired
	private AuthenticationData authData;

	public TrackingStatusRequest() {

	}

	@JsonProperty("AuthenticationData")
	public AuthenticationData getAuthData() {
		return authData;
	}

	public void setAuthData(AuthenticationData authData) {
		this.authData = authData;
	}

}
