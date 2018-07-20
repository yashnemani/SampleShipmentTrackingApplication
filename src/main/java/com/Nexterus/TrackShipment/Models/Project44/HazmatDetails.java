package com.Nexterus.TrackShipment.Models.Project44;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class HazmatDetails {

	private List<String> hazardClasses;

	public HazmatDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public HazmatDetails(List<String> hazardClasses) {
		super();
		this.hazardClasses = hazardClasses;
	}

	@JsonProperty("hazardClasses")
	public List<String> getHazardClasses() {
		return hazardClasses;
	}
}
