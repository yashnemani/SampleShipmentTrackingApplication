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
		banStatus.setLoadID(13610667);
		banStatus.setBOL("0001645560");
		banStatus.setProNumber("10272227820");
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
		banStatus.setCode("L1");
		banStatus.setCarrierMessage("Load Trailer: 286286");
		banStatus.setBanyanMessage("Loading");
		banStatus.setCity("Phoenix");
		banStatus.setState("AZ");
		banyanStatuses.add(banStatus);
		BanyanStatus banStatus1 = new BanyanStatus();
		banStatus1.setLoadID(13610667);
		banStatus1.setBOL("0001645560");
		banStatus1.setProNumber("10272227820");
		banStatus1.setCity("Phoenix");
		banStatus1.setState("AZ");
		banStatus1.setCarrierMessage("Delivery Appointment 00/00/00");
		banStatus1.setBanyanMessage("Delivery Appointment");
		banStatus1.setCode("AB");
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
