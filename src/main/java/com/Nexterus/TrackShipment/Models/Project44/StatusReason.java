package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class StatusReason {

	private String code;
	private String description;
	public StatusReason() {
		super();
		// TODO Auto-generated constructor stub
	}
	@JsonProperty("code")
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
}
