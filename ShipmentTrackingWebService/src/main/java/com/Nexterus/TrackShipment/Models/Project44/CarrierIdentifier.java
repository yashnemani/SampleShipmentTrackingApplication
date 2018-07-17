package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class CarrierIdentifier {

	private String type;
	private String value;

	public CarrierIdentifier() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarrierIdentifier(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
