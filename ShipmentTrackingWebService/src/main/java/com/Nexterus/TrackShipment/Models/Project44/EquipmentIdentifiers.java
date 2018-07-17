package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class EquipmentIdentifiers {

	private String type;
	private String value;

	public EquipmentIdentifiers() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EquipmentIdentifiers(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	@JsonProperty("type")
	public String getType() {
		return type;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}
}
