package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TruckDetails {

	@Autowired
	private TruckDimensions truckDimensions;
	private int weight ;
	private String weightUnitOfMeasure;

	public TruckDetails() {
		super();
		this.weight=1;
		this.weightUnitOfMeasure="LB";
	}

	@JsonProperty("truckDimensions")
	public TruckDimensions getTruckDimensions() {
		return truckDimensions;
	}

	public void setTruckDimensions(TruckDimensions truckDimensions) {
		this.truckDimensions = truckDimensions;
	}

	@JsonProperty("weight")
	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	@JsonProperty("weightUnitOfMeasure")
	public String getWeightUnitOfMeasure() {
		return weightUnitOfMeasure;
	}

	public void setWeightUnitOfMeasure(String weightUnitOfMeasure) {
		this.weightUnitOfMeasure = weightUnitOfMeasure;
	}
}
