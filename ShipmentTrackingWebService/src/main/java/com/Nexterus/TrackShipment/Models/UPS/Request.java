package com.Nexterus.TrackShipment.Models.UPS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Request {
	
	private String RequestOption;
	@Autowired
	private TransactionReference transactionRef;

	public Request() {
		RequestOption = "1";
	}

	@JsonProperty("RequestOption")
	public String getRequestOption() {
		return RequestOption;
	}

	public void setRequestOption(String requestOption) {
		RequestOption = requestOption;
	}

	@JsonProperty("TransactionReference")
	public TransactionReference getTransactionRef() {
		return transactionRef;
	}

	public void setTransactionRef(TransactionReference transactionRef) {
		this.transactionRef = transactionRef;
	}

}
