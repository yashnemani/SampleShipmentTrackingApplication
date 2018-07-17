package com.Nexterus.TrackShipment.Models.Project44;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class CapacityProviderAccountGroup {

	private String code;
	private List<Account44> account44s;
	
	public CapacityProviderAccountGroup() {
		super();
		// TODO Auto-generated constructor stub
	}
	public CapacityProviderAccountGroup(String code, List<Account44> account44s) {
		super();
		this.code = code;
		this.account44s = account44s;
	}
	@JsonProperty("code")
	public String getCode() {
		return code;
	}
	@JsonProperty("accounts")
	public List<Account44> getAccounts() {
		return account44s;
	}
	
}
