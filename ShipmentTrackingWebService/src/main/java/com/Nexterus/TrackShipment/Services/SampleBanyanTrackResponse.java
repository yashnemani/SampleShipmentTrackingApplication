package com.Nexterus.TrackShipment.Services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Models.Banyan.BanyanStatus;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusResponse;

@Service
public class SampleBanyanTrackResponse {

	public TrackingStatusResponse getSampleBanyanTrackresponse() {
		TrackingStatusResponse trackResponseSample = new TrackingStatusResponse();
		trackResponseSample.setError(null);
		trackResponseSample.setSuccess(true);
		List<BanyanStatus> banyanStatuses = new ArrayList<>();
		BanyanStatus banStatus = new BanyanStatus();
		banStatus.setLoadID(13607130);
		banStatus.setBOL("21758022");
		banStatus.setProNumber("1057218806");
		String dt = "2018-05-07T17:06:13.050+0000";
		String date = dt.substring(0, 10);
		String time = dt.substring(11, 19);
		DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		java.sql.Timestamp dateTime = null;
		try {
			dateTime = new java.sql.Timestamp(dateTimeFormatter.parse(date + " " + time).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		banStatus.setDateTime(dateTime);
		banStatus.setCode("D1");
		banStatus.setCarrierMessage("Manually moved to Delivered on May  7 2018  5:06PM by Automation");
		banStatus.setBanyanMessage("Completed Unloading at Delivery Location");
		banStatus.setCity("DENVER");
		banStatus.setState("CO");
		banyanStatuses.add(banStatus);
		BanyanStatus banStatus1 = new BanyanStatus();
		banStatus1.setLoadID(13609404);
		banStatus1.setBOL("0001636518");
		banStatus1.setProNumber("102722260705");
		banStatus1.setCity("DENVER");
		banStatus1.setState("CO");
		banStatus1.setCarrierMessage("Dispatched for Delivery");
		banStatus1.setBanyanMessage("En Route to Delivery Location");
		banStatus1.setCode("X6");
		dt = "2018-04-24T13:53:00.000+0000";
		date = dt.substring(0, 10);
		time = dt.substring(11, 19);
		try {
			dateTime = new java.sql.Timestamp(dateTimeFormatter.parse(date + " " + time).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		banStatus1.setDateTime(dateTime);
		banyanStatuses.add(banStatus1);
		trackResponseSample.setTrackingStatuses(banyanStatuses);
		return trackResponseSample;
	}
}
