package com.Nexterus.TrackShipment.Models.XPO;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class XPOAccess {

	private String grant_type;
	private String username;
	private String password;
	private String userToken;
	private String tokenUrl;
	
	
	public String getTokenUrl() {
		return tokenUrl;
	}

	public String getUserToken() {
		return userToken;
	}

	public XPOAccess() {
		super();
		this.grant_type = "password";
		this.username = "mroberts4";
		this.password = "nxtCNWY802";
		this.userToken = "Basic M040UjBVMlJaVUhBcTlDSFZncWhzb1Y2VkM0YTpUaTBlUGRYX3ppenhtckN3MmxVRFVOcDFfdE1h";
		this.tokenUrl = "https://api.ltl.xpo.com/token";
	}
	
	@JsonProperty("grant_type")
	public String getGrant_type() {
		return grant_type;
	}

	
	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}
	
	
	
}
