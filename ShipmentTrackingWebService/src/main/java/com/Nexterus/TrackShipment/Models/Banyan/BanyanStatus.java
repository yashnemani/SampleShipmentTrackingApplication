package com.Nexterus.TrackShipment.Models.Banyan;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class BanyanStatus {

	public Integer LoadID;
	public String BOL;
	public String ProNumber;
	public java.sql.Timestamp DateTime;
	public String Code;
	public String CarrierMessage;
	public String BanyanMessage;
	public String City;
	public String State;
	
	public BanyanStatus() {
	}
	

	
	@JsonProperty("LoadID")
	public Integer getLoadId() {
		return LoadID;
	}

	@JsonProperty("BOL")
	public String getBOL() {
		return BOL;
	}

	@JsonProperty("ProNumber")
	public String getProNumber() {
		return ProNumber;
	}

	@JsonProperty("DateTime")
	public java.sql.Timestamp getDateTime() {
		return DateTime;
	}
 
	@JsonProperty("Code")
	public String getCode() {
		return Code;
	}

	@JsonProperty("CarrierMessage")
	public String getCarrierMessage() {
		return CarrierMessage;
	}

	@JsonProperty("BanyanMessage")
	public String getBanyanMessage() {
		return BanyanMessage;
	}

	@JsonProperty("City")
	public String getCity() {
		return City;
	}

	@JsonProperty("State")
	public String getState() {
		return State;
	}



	public void setLoadID(Integer loadID) {
		LoadID = loadID;
	}



	public void setBOL(String bOL) {
		BOL = bOL;
	}



	public void setProNumber(String proNumber) {
		ProNumber = proNumber;
	}



	public void setDateTime(java.sql.Timestamp dateTime) {
		DateTime = dateTime;
	}



	public void setCode(String code) {
		Code = code;
	}



	public void setCarrierMessage(String carrierMessage) {
		CarrierMessage = carrierMessage;
	}



	public void setBanyanMessage(String banyanMessage) {
		BanyanMessage = banyanMessage;
	}



	public void setCity(String city) {
		City = city;
	}



	public void setState(String state) {
		State = state;
	}

}
