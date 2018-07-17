package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class ShipmentStops {

	private Integer stopNumber;
	@Autowired
	private AppointmentWindow appointmentWindow;
	@Autowired
	private Location44 location;
	private String stopName;
	@Autowired
	private GeoCoordinates geoCoordinates;

	public ShipmentStops() {
		super();
	}

	public ShipmentStops(Builder build) {
		this.stopNumber = build.stopNumber;
		this.appointmentWindow = build.appointmentWindow;
		this.location = build.location;
		this.stopName = build.stopName;
		this.geoCoordinates = build.geoCoordinates;
	}

	public static class Builder {
		private Integer stopNumber;
		private AppointmentWindow appointmentWindow;
		private Location44 location;
		private String stopName;
		private GeoCoordinates geoCoordinates;

		public Builder setStopNumber(Integer stopNumber) {
			this.stopNumber = stopNumber;
			return this;
		}

		public Builder setAppointmentWindow(AppointmentWindow appointmentWindow) {
			this.appointmentWindow = appointmentWindow;
			return this;
		}

		public Builder setLocation(Location44 location) {
			this.location = location;
			return this;
		}

		public Builder setStopName(String stopName) {
			this.stopName = stopName;
			return this;
		}

		public Builder setGeoCoordinates(GeoCoordinates geoCoordinates) {
			this.geoCoordinates = geoCoordinates;
			return this;
		}

		public ShipmentStops build() {
			return new ShipmentStops(this);
		}
	}

	@JsonProperty("stopNumber")
	public Integer getStopNumber() {
		return stopNumber;
	}

	@JsonProperty("appointmentWindow")
	public AppointmentWindow getAppointmentWindow() {
		return appointmentWindow;
	}

	@JsonProperty("location")
	public Location44 getLocation() {
		return location;
	}

	@JsonProperty("stopName")
	public String getStopName() {
		return stopName;
	}

	@JsonProperty("geoCoordinates")
	public GeoCoordinates getGeoCoordinates() {
		return geoCoordinates;
	}
}
