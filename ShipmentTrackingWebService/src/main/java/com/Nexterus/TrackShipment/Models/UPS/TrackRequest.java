package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TrackRequest {

	@Autowired
	private Request req;
	private String inquiryNumber;
	public TrackRequest() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TrackRequest(Request req, String inquiryNumber) {
		super();
		this.req = req;
		this.inquiryNumber = inquiryNumber;
	}
	
	@JsonProperty("Request")
	public Request getReq() {
		return req;
	}
	public void setReq(Request req) {
		this.req = req;
	}
	
	@JsonProperty("InquiryNumber")
	public String getInquiryNumber() {
		return inquiryNumber;
	}
	public void setInquiryNumber(String inquiryNumber) {
		this.inquiryNumber = inquiryNumber;
	}
	
	
}
