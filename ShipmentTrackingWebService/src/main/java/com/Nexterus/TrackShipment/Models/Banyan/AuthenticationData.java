package com.Nexterus.TrackShipment.Models.Banyan;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class AuthenticationData {

	private String Username;
	private String Password;
	private String ClientRefNum;
	
	public AuthenticationData() {
		this.Username = "NexterusWS";
		this.Password ="N3Xt3Ru5W5";
		this.ClientRefNum= null;
	}

	@JsonProperty("Username")
	public String getUsername() {
		return Username;
	}
	
	@JsonProperty("Password")
	public String getPassword() {
		return Password;
	}
	
	@JsonProperty("ClientRefNum")
	public String getClientRefNum() {
		return ClientRefNum;
	}
	
	
	
	
}
