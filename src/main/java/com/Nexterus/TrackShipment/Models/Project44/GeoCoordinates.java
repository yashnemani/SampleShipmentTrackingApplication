package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class GeoCoordinates {

	private Double latitude;
	private Double longitude;

	public GeoCoordinates() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GeoCoordinates(Double latitude, Double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	@JsonProperty("latitude")
	public Double getLatitude() {
		return latitude;
	}

	@JsonProperty("longitude")
	public Double getLongitude() {
		return longitude;
	}
}
