package com.Nexterus.TrackShipment.Models.Project44;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TrackLoadStatusResponse {


	@Autowired
	private InitializeTruckLoad shipment;
	private List<InfoMessage> infoMessages;
	private StatusUpdate latestStatusUpdate;
	private List<StatusUpdate> statusUpdates;
	private List<StopStatus> latestStopStatuses;
	private String mapUrl;

	public TrackLoadStatusResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@JsonProperty("latestStatusUpdate")
	public StatusUpdate getLatestStatusUpdate() {
		return latestStatusUpdate;
	}


	public void setLatestStatusUpdate(StatusUpdate latestStatusUpdate) {
		this.latestStatusUpdate = latestStatusUpdate;
	}

	@JsonProperty("statusUpdates")
	public List<StatusUpdate> getStatusUpdates() {
		return statusUpdates;
	}


	public void setStatusUpdates(List<StatusUpdate> statusUpdates) {
		this.statusUpdates = statusUpdates;
	}

	@JsonProperty("latestStopStatuses")
	public List<StopStatus> getLatestStopStatuses() {
		return latestStopStatuses;
	}


	public void setLatestStopStatuses(List<StopStatus> latestStopStatuses) {
		this.latestStopStatuses = latestStopStatuses;
	}

	@JsonProperty("mapUrl")
	public String getMapUrl() {
		return mapUrl;
	}


	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}


	@JsonProperty("shipment")
	public InitializeTruckLoad getShipment() {
		return shipment;
	}
	public void setShipment(InitializeTruckLoad shipment) {
		this.shipment = shipment;
	}
	@JsonProperty("infoMessages")
	public List<InfoMessage> getInfoMessages() {
		return infoMessages;
	}
	public void setInfoMessages(List<InfoMessage> infoMessages) {
		this.infoMessages = infoMessages;
	}
}
