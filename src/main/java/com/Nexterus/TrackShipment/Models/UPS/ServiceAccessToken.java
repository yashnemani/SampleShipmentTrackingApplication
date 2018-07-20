package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ServiceAccessToken {

	private String AccessLicenseNumber;

	public ServiceAccessToken() {
		AccessLicenseNumber = "9D02769EE10F1958";
	}

	@JsonProperty("AccessLicenseNumber")
	public String getAccessLicenseNumber() {
		return AccessLicenseNumber;
	}

}
