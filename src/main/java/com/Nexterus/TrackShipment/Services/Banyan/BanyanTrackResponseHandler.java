package com.Nexterus.TrackShipment.Services.Banyan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Controllers.TrackingController;
import com.Nexterus.TrackShipment.Entities.BanyanTrackingResponse;
import com.Nexterus.TrackShipment.Models.Banyan.BanyanStatus;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusResponse;
import com.Nexterus.TrackShipment.Repos.BanyanTrackingResponseRepository;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;

@Service
public class BanyanTrackResponseHandler {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(BanyanTrackResponseHandler.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	
	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	BanyanStatusHandlerService statusHandlerService;
	@Autowired
	BanyanTrackingResponseRepository saveResponseRepo;
	@Autowired
	TrackingController trackController;

	public void handleTrackResponse(TrackingStatusResponse trackResponse) {

		if (!trackResponse.isSuccess()) {
			log.error("Error: " + trackResponse.getError());
			return;
		}

		if (trackResponse.getTrackingStatuses() == null) {
			log.error("Banyan has returned no Tracking Statuses!");
			return;
		} else if (trackResponse.getTrackingStatuses().size() == 0) {
			log.error("Banyan has returned no Tracking Statuses!");
			return;
		}
		// Save Banyan Track Response
		saveBanyanTrackResponse(trackResponse);

		List<BanyanStatus> banyanStatuses = new ArrayList<>();
		BanyanStatus banStatus = new BanyanStatus();
		banyanStatuses = trackResponse.getTrackingStatuses();

		for (int i = trackResponse.getTrackingStatuses().size() - 1; i >= 0; i--) {
			banStatus = banyanStatuses.get(i);
			log.info(i + " " + banStatus.getCode());
				statusHandlerService.handleLoadStatus(banStatus);
		}
	}

	public TrackingStatusResponse getBanyanResponse(int id) {

		TrackingStatusResponse trackResponseSample = new TrackingStatusResponse();
		if (!saveResponseRepo.existsById(id)) {
			log.info("No saved response with given ID");
			return null;
		}
		BanyanTrackingResponse banyanTrackResponse = new BanyanTrackingResponse();
		banyanTrackResponse = saveResponseRepo.findById(id).get();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(banyanTrackResponse.getTrackResponse());
			ObjectInputStream his = new ObjectInputStream(in);
			trackResponseSample = (TrackingStatusResponse) his.readObject();
		} catch (ClassNotFoundException | IOException e) {
			nxtLogger.error("Deserialize Track Response " + e.getCause().getMessage());
		}
		return trackResponseSample;
	}

	public void saveBanyanTrackResponse(TrackingStatusResponse obj) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] yourBytes = bos.toByteArray();
			BanyanTrackingResponse banyanTrackResponse = new BanyanTrackingResponse();
			banyanTrackResponse.setTrackResponse(yourBytes);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			banyanTrackResponse.setTimestamp(timestamp);
			saveResponseRepo.save(banyanTrackResponse);
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				nxtLogger.error(e.getStackTrace().toString());
			}
		}
	}
}
