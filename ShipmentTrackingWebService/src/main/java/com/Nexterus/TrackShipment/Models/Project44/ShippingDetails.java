package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ShippingDetails {

	private boolean multipleDrivers;
	@Autowired
	private TruckDetails truckDetails;
	private HazmatDetails hazmatDetails;

	public ShippingDetails() {
		super();
		multipleDrivers = false;
	}

	@JsonProperty("multipleDrivers")
	public boolean isMultipleDrivers() {
		return multipleDrivers;
	}

	public void setMultipleDrivers(boolean multipleDrivers) {
		this.multipleDrivers = multipleDrivers;
	}

	@JsonProperty("truckDetails")
	public TruckDetails getTruckDetails() {
		return truckDetails;
	}

	public void setTruckDetails(TruckDetails truckDetails) {
		this.truckDetails = truckDetails;
	}

	@JsonProperty("hazmatDetails")
	public HazmatDetails getHazmatDetails() {
		return hazmatDetails;
	}

	public void setHazmatDetails(HazmatDetails hazmatDetails) {
		this.hazmatDetails = hazmatDetails;
	}
}
