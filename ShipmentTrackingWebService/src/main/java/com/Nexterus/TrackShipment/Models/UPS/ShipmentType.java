package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ShipmentType {

	private String code;

	public ShipmentType() {
		setCode("02");
	}

	@JsonProperty("Code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
