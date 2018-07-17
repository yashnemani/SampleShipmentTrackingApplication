package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ApiConfiguration {

	private boolean fallBackToDefaultAccountGroup;
	
	public ApiConfiguration() {
		super();
		this.fallBackToDefaultAccountGroup = true;
	}

	public ApiConfiguration(boolean fallBackToDefaultAccountGroup) {
		super();
		this.fallBackToDefaultAccountGroup = fallBackToDefaultAccountGroup;
	}

	@JsonProperty("fallBackToDefaultAccountGroup")
	public boolean isFallBackToDefaultAccountGroup() {
		return fallBackToDefaultAccountGroup;
	}
	
	
}
