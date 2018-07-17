package com.Nexterus.TrackShipment.Models.Project44;

import java.util.Base64;

public class Auth44 {
	private String username;
	private String password;

	public Auth44() {
		super();
		this.username = "ynemani@nexterus.com";
		this.password = "smg94@psycho1";
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getBasic() {
		String input = username + ":" + password;
		String encodedString = Base64.getEncoder().encodeToString(input.getBytes());
		return "Basic " + encodedString;
	}
}
