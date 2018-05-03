package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class UsernameToken {

	private String user;
	private String pass;

	public UsernameToken() {
		user="TBBGL";
		pass="Nexterus802";
		
	}

	@JsonProperty("Username")
	public String getUser() {
		return user;
	}

	@JsonProperty("Password")
	public String getPass() {
		return pass;
	}

}
