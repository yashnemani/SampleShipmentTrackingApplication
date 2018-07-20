package com.Nexterus.TrackShipment.Services.General;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingCurrentStatus;
import com.Nexterus.TrackShipment.Entities.BookingReferences;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Entities.NxtStatusDates;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;
import com.Nexterus.TrackShipment.Services.Banyan.BanyanTrackResponseHandler;
import com.Nexterus.TrackShipment.Services.UPS.UpsTrackResponseHandler;
import com.Nexterus.TrackShipment.Services.XPO.XpoTrackResponseHandler;
import com.google.gson.Gson;

@Service
public class TrackingResponseHandler {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(TrackingResponseHandler.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");

	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	XpoTrackResponseHandler xpoHandler;
	@Autowired
	UpsTrackResponseHandler upsHandler;
	@Autowired
	BanyanTrackResponseHandler banyanHandler;
	@Autowired
	TrackSchedulerService trackSchedulerService;

	@Transactional
	public void handleTrackingResponse(Object obj, int id, int provider) {

		Gson gson = new Gson();
		String json = gson.toJson(obj);
		String status = null;
		BookingStatus bookingStatus = new BookingStatus();
		JSONObject jobj;

		try {
			jobj = new JSONObject(json);
		} catch (JSONException e) {
			nxtLogger.error(e.getCause() + " " + e.getMessage());
			return;
		}

		if (provider == 1) {
			bookingStatus = xpoHandler.handleXpoTrackResponse(jobj);
		}
		if (provider == 2) {
			bookingStatus = upsHandler.handleTrackResponse(jobj);
		}
		if (bookingStatus == null)
			return;

		if (!bookRepo.existsById(id)) {
			nxtLogger.error("Booking does not exist for ID. " + id + " could be Reference Number");
			return;
		}
		Booking booking = bookRepo.getOne(id);

		status = bookingStatus.getStatus();
		String EdiStatus = bookStatusRepo.findEdiStatus(provider, status);
		String NxtStatus = null;
		if (EdiStatus == null) {
			nxtLogger.error("Cannot find EDI status mapping for carrier status code");
			return;
		}

		NxtStatus = bookStatusRepo.findNxtStatus(EdiStatus);
		if (NxtStatus == null) {
			log.warn("Cannot find Nxt Status mapping for EDI status code");
			return;
		}

		NxtStatusDates statusDates = new NxtStatusDates();
		if (booking.getStatusDates() != null)
			statusDates = booking.getStatusDates();
		java.sql.Timestamp pkupDate = null;
		java.sql.Timestamp dlvrDate = null;

		if (statusDates.getBooking() != null) {
			if (statusDates.getDt_pickedup() == null) {
				if (provider == 1)
					pkupDate = xpoHandler.getPickupDate(jobj);
				if (provider == 2)
					pkupDate = upsHandler.getPickupDate(jobj);
				statusDates.setDt_pickedup(pkupDate);
			}
			if (EdiStatus.equals("D1") && statusDates.getDt_delivered() == null) {
				dlvrDate = bookingStatus.getDate();
				statusDates.setDt_delivered(dlvrDate);
			}
		} else {
			statusDates.setBooking(booking);
			if (provider == 1)
				pkupDate = xpoHandler.getPickupDate(jobj);
			if (provider == 2)
				pkupDate = upsHandler.getPickupDate(jobj);
			statusDates.setDt_pickedup(pkupDate);
			if (EdiStatus.equals("D1")) {
				dlvrDate = bookingStatus.getDate();
				statusDates.setDt_delivered(dlvrDate);
			}
		}
		booking.setStatusDates(statusDates);

		List<BookingStatus> statuses = new ArrayList<>();
		bookingStatus.setStatus(EdiStatus);
		bookingStatus.setBooking(booking);
		statuses.add(bookingStatus);
		booking.setStatuses(statuses);

		BookingCurrentStatus currentStatus = new BookingCurrentStatus();
		if (booking.getCurrentStatus() != null) {
			currentStatus = booking.getCurrentStatus();
		}
		currentStatus.setBooking(booking);
		currentStatus.setLocation(bookingStatus.getLocation());
		currentStatus.setMessage(bookingStatus.getMessage());
		currentStatus.setStatus(bookingStatus);
		currentStatus.setShipStatus(EdiStatus);
		currentStatus.setShipState(NxtStatus);
		currentStatus.setDate(bookingStatus.getDate());
		currentStatus.setLastUpdatedDt();
		if (provider == 1) {
			Timestamp xpoEstDelivery = xpoHandler.getEstDeliveryDt();
			if (xpoEstDelivery != null)
				currentStatus.setEstDeliveryDt(xpoEstDelivery);
		} else if (provider == 2) {
			Timestamp upsEstDelivery = upsHandler.getEstDelivery();
			if (upsEstDelivery != null)
				currentStatus.setEstDeliveryDt(upsEstDelivery);
		}
		booking.setCurrentStatus(currentStatus);

		if (provider == 2) {
			if (booking.getReferences() != null) {
				if (!booking.getReferences().stream().filter(a -> a.getRef_type() == 0).findFirst().isPresent()) {
					if (upsHandler.getPro() != null) {
						BookingReferences refs = new BookingReferences();
						refs.setBooking(booking);
						refs.setRef_type(0);
						refs.setReference(upsHandler.getPro());
						booking.getReferences().add(refs);
					}
				}
			}
		}

		// Delete Booking from TrackingQueue if status is delivered
		if (EdiStatus.equals("D1") || EdiStatus.equals("CA")) {
			trackSchedulerService.trackDeliveredCount(provider);
			bookRepo.deleteFromTrackingQueue(id);
		}

		bookRepo.save(booking);
		bookRepo.refresh(booking);
	}
}
