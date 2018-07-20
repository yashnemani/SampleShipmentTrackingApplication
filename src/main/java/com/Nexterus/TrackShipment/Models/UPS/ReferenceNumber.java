package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ReferenceNumber {

	private String value;

	public ReferenceNumber() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("Value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
