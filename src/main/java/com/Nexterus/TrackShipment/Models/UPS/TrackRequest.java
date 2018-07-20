package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TrackRequest {

	@Autowired
	private Request req;
	@Autowired
	private PickupDateRange pickupDateRange;
	@Autowired
	private ShipmentType shipType;
	@Autowired
	private ReferenceNumber refNum;
	private String shipperNumber;

	public void setShipperNumber() {
		this.shipperNumber = "1F346A";
	}

	@JsonProperty("ShipperNumber")
	public String getShipperNumber() {
		return shipperNumber;
	}

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

	@JsonProperty("PickupDateRange")
	public PickupDateRange getPickupDateRange() {
		return pickupDateRange;
	}

	public void setPickupDateRange(PickupDateRange pickupDateRange) {
		this.pickupDateRange = pickupDateRange;
	}

	@JsonProperty("ShipmentType")
	public ShipmentType getShipType() {
		return shipType;
	}

	public void setShipType(ShipmentType shipType) {
		this.shipType = shipType;
	}

	@JsonProperty("ReferenceNumber")
	public ReferenceNumber getRefNum() {
		return refNum;
	}

	public void setRefNum(ReferenceNumber refNum) {
		this.refNum = refNum;
	}

}
