package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TransactionReference {

	private String CustomerContext;

	public TransactionReference() {
		CustomerContext = "This is a UPGF Shipment!";
	}

	@JsonProperty("CustomerContext")
	public String getCustomerContext() {
		return CustomerContext;
	}

	public void setCustomerContext(String customerContext) {
		CustomerContext = customerContext;
	}
}
