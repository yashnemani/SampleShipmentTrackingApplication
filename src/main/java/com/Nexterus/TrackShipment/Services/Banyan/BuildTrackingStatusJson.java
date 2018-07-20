package com.Nexterus.TrackShipment.Services.Banyan;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Controllers.TrackingController;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Models.Status;
import com.Nexterus.TrackShipment.Models.Tracking;
import com.Nexterus.TrackShipment.Models.TrackingStatusJson;
import com.Nexterus.TrackShipment.Repos.BookingRepository;

@Service
public class BuildTrackingStatusJson {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(BuildTrackingStatusJson.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	TrackingStatusJson trackingStatusJson;
	@Autowired
	TrackingController trackController;

	public TrackingStatusJson updateTrackingStatuses(String scac, String RqId, String proNo,
			List<BookingStatus> statuses) {
		if (statuses == null || scac == null || RqId == null || proNo == null) {
			BookingStatus statuz = new BookingStatus();
			statuz.setLocation("Hyderabad");
			statuz.setState("IN");
			statuz.setStatus("D1");
			Timestamp time = new Timestamp(System.currentTimeMillis());
			statuz.setDate(time);
			scac = "RDFS";
			RqId = "5412552";
			proNo = "365862226";
			List<BookingStatus> statuses1 = new ArrayList<>();
			statuses1.add(statuz);
			statuses = statuses1;
		}
		if (bookRepo.getUpdateStatusFlag(scac).equals("N")) {
			log.info("Update Old Status Flag for Carrier " + scac + " is false");
			return null;
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
		String formattedDate = null;
		List<Status> statusList = new ArrayList<>();
		for (int i = 0; i < statuses.size(); i++) {
			Status status1 = new Status();
			status1.setStatusCity(statuses.get(i).getLocation());
			status1.setStatusCode(statuses.get(i).getStatus());
			formattedDate = dateFormat.format(statuses.get(i).getDate());
			status1.setStatusDate(formattedDate);
			status1.setStatusState(statuses.get(i).getState());
			statusList.add(status1);
		}

		Tracking track = new Tracking();
		track.setProNum(proNo);
		track.setRtQteId(RqId);
		track.setStatus(statusList);
		List<Tracking> trackList = new ArrayList<>();
		trackList.add(track);
		trackingStatusJson.setScac(scac);
		trackingStatusJson.setTrackingInfo(trackList);
		return trackController.updateOldStatus(trackingStatusJson);
		/* return trackingStatusJson; */
	}
}
