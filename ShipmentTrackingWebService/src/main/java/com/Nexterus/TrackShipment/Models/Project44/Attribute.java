package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Attribute {
	private String name;
	private String value;

	public Attribute(String name, String value) {
		super();
		this.name = name;
		this.value = value;
	}

	public Attribute() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("name")
	public String getname() {
		return name;
	}

	public void setname(String name) {
		this.name = name;
	}

	@JsonProperty("value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
