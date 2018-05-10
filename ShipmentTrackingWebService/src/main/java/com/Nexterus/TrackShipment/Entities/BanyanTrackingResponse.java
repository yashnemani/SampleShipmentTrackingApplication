package com.Nexterus.TrackShipment.Entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="banyan_track_response")
public class BanyanTrackingResponse {

	@Id
	@Column(name="id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private int id;
	
	@Column(name="TRACK_RESPONSE")
	private byte[] trackResponse;
	
	@Column(name="TIMESTAMP",nullable = false)
	private java.sql.Timestamp timestamp;

	public BanyanTrackingResponse() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public byte[] getTrackResponse() {
		return trackResponse;
	}

	public void setTrackResponse(byte[] trackResponse) {
		this.trackResponse = trackResponse;
	}

	public java.sql.Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(java.sql.Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
