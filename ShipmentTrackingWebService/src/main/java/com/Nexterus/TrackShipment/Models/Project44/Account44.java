package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Account44 {

	private String code;

	public Account44(String code) {
		super();
		this.code = code;
	}

	public Account44() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("code")
	public String getCode() {
		return code;
	}

}
