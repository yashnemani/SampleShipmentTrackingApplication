package com.Nexterus.TrackShipment.Services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
			System.err.println("Banyan Track Status Response has failed");
			System.err.println("Error: " + trackResponse.getError());
			return;
		}

		if (trackResponse.getTrackingStatuses() == null) {
			System.err.println("Banyan has returned no Tracking Statuses!");
			return;
		} else if (trackResponse.getTrackingStatuses().size() == 0) {
			System.err.println("Banyan has returned no Tracking Statuses!");
			return;
		}
		// Save Banyan Track Response
		saveBanyanTrackResponse(trackResponse);

		List<BanyanStatus> banyanStatuses = new ArrayList<>();
		BanyanStatus banStatus = new BanyanStatus();
		banyanStatuses = trackResponse.getTrackingStatuses();

		for (int i = trackResponse.getTrackingStatuses().size() - 1; i >= 0; i--) {
			banStatus = banyanStatuses.get(i);
			System.out.println(i + " " + banStatus.getCode());
			statusHandlerService.handleLoadStatus(banStatus);
		}
	}

	public TrackingStatusResponse getBanyanResponse(int id) {

		TrackingStatusResponse trackResponseSample = new TrackingStatusResponse();
		if (!saveResponseRepo.existsById(id)) {
			System.out.println("No saved response with given ID");
			return null;
		}
		BanyanTrackingResponse banyanTrackResponse = new BanyanTrackingResponse();
		banyanTrackResponse = saveResponseRepo.findById(id).get();
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(banyanTrackResponse.getTrackResponse());
			ObjectInputStream his = new ObjectInputStream(in);
			trackResponseSample = (TrackingStatusResponse) his.readObject();
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Deserialize Track Response " + e.getCause().getMessage());
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
				e.printStackTrace();
			}
		}
	}

	public List<BanyanStatus> findBanyanUnknownStatuses() {

		List<BigDecimal> blobIds = new ArrayList<>();
		Set<String> unknownStatuses = new HashSet<>();
		List<BanyanStatus> unknownLoads = new ArrayList<>();
		blobIds = bookRepo.getBlobIds();
		
		for (int i = 0; i < blobIds.size(); i++) {
			TrackingStatusResponse track = new TrackingStatusResponse();
			track = trackController.getTrackResponseBlob(blobIds.get(i).intValue());
			List<BanyanStatus> banyanStatuses = new ArrayList<>();
			BanyanStatus banStatus = new BanyanStatus();
			banyanStatuses = track.getTrackingStatuses();
			
			for (int j = banyanStatuses.size() - 1; j >= 0; j--) {
				banStatus = banyanStatuses.get(j);
				String code = banStatus.getCode();
				String NxtStatus = bookStatusRepo.findNxtStatus(code);
				if (NxtStatus == null) {
					System.err.println("EDI Status " + code + " does not exist");
					if (!unknownStatuses.contains(code))
						unknownLoads.add(banStatus);
					unknownStatuses.add(code);
				}
			}
		}

		return unknownLoads;
	}
}
