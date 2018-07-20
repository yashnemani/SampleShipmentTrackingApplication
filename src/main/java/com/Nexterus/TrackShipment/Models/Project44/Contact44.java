package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Contact44 {

private String companyName;
private String contactName;
private String phoneNumber;
private String phoneNumber2;
private String email;
private String faxNumber;
public Contact44() {
	super();
}

public Contact44(Builder build) {
this.companyName=build.companyName;
this.contactName=build.contactName;
this.phoneNumber=build.phoneNumber;
this.phoneNumber2=build.phoneNumber2;
this.email=build.email;
this.faxNumber=build.faxNumber;
}

public static class Builder{
	
	private String companyName;
	private String contactName;
	private String phoneNumber;
	private String phoneNumber2;
	private String email;
	private String faxNumber;
	
	public Builder setCompanyName(String companyName) {
		this.companyName = companyName;
		return this;
	}
	public Builder setContactName(String contactName) {
		this.contactName = contactName;
		return this;
	}
	public Builder setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}
	public Builder setPhoneNumber2(String phoneNumber2) {
		this.phoneNumber2 = phoneNumber2;
		return this;
	}
	public Builder setEmail(String email) {
		this.email = email;
		return this;
	}
	public Builder setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
		return this;
	}
	
	public Contact44 build() {
		return new Contact44(this);
	}
}
@JsonProperty("companyName")
public String getCompanyName() {
	return companyName;
}
@JsonProperty("contactName")
public String getContactName() {
	return contactName;
}
@JsonProperty("phoneNumber")
public String getPhoneNumber() {
	return phoneNumber;
}
@JsonProperty("phoneNumber2")
public String getPhoneNumber2() {
	return phoneNumber2;
}
@JsonProperty("email")
public String getEmail() {
	return email;
}
@JsonProperty("faxNumber")
public String getFaxNumber() {
	return faxNumber;
}
}
