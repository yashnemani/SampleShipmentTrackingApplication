package com.Nexterus.TrackShipment.Models;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Tracking {

	private String rtQteId;
	private String proNum;
	private List<Status> status;
	


	public void setProNum(String proNum) {
		this.proNum = proNum;
	}
	@JsonProperty("status")
	public List<Status> getStatus() {
		return status;
	}

	public void setStatus(List<Status> status) {
		this.status = status;
	}

	public void setRtQteId(String rtQteId) {
		this.rtQteId = rtQteId;
	}

	public Tracking() {
		super();
		// TODO Auto-generated constructor stub
	}

	@JsonProperty("rtQteId")
	public String getRtQteId() {
		return rtQteId;
	}

	@JsonProperty("proNum")
	public String getProNo() {
		return proNum;
	}
	
}
