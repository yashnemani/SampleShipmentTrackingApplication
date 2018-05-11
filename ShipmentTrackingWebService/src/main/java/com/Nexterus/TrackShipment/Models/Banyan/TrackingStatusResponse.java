package com.Nexterus.TrackShipment.Models.Banyan;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TrackingStatusResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Autowired
	private List<BanyanStatus> TrackingStatuses;
	private boolean Success;
	private String Error;

	@JsonProperty("TrackingStatuses")
	public List<BanyanStatus> getTrackingStatuses() {
		return TrackingStatuses;
	}

	public void setTrackingStatuses(List<BanyanStatus> trackingStatuses) {
		TrackingStatuses = trackingStatuses;
	}

	@JsonProperty("Success")
	public boolean isSuccess() {
		return Success;
	}

	public void setSuccess(boolean success) {
		Success = success;
	}

	@JsonProperty("Error")
	public String getError() {
		return Error;
	}

	public void setError(String error) {
		Error = error;
	}
}
