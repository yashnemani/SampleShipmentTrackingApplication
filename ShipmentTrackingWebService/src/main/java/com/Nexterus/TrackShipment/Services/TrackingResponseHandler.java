package com.Nexterus.TrackShipment.Services;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingCurrentStatus;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Entities.NxtStatusDates;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;
import com.google.gson.Gson;

@Service
public class TrackingResponseHandler {

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
			System.out.println(e.getCause() + " " + e.getMessage());
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
			System.out.println("Booking does not exist for ID. " + id + " could be Reference Number");
			return;
		}
		Booking booking = bookRepo.getOne(id);

		status = bookingStatus.getStatus();
		System.out.println("Carrier Status Code: " + status);
		String EdiStatus = bookStatusRepo.findEdiStatus(provider, status);
		String NxtStatus = null;
		if (EdiStatus == null) {
			System.out.println("Cannot find EDI status mapping for carrier status code");
			return;
		}
		System.out.println("EDI Status Code: " + EdiStatus);
		NxtStatus = bookStatusRepo.findNxtStatus(EdiStatus);
		if (NxtStatus == null) {
			System.out.println("Cannot find Nxt Status mapping for EDI status code");
			return;
		}
		System.out.println("Nexterus Status Code: " + NxtStatus);

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

		Set<BookingStatus> statuses = new HashSet<>();
		bookingStatus.setStatus(EdiStatus);
		bookingStatus.setBooking(booking);
		statuses.add(bookingStatus);
		booking.setStatuses(statuses);

		BookingCurrentStatus currentStatus = new BookingCurrentStatus();
		if (booking.getCurrentStatus() != null) {
			System.out.println("Update Current Status");
			currentStatus = booking.getCurrentStatus();
		}
		currentStatus.setBooking(booking);
		currentStatus.setLocation(bookingStatus.getLocation());
		currentStatus.setMessage(bookingStatus.getMessage());
		currentStatus.setStatus(bookingStatus);
		currentStatus.setShipStatus(EdiStatus);
		currentStatus.setShipState(NxtStatus);
		currentStatus.setDate(bookingStatus.getDate());
		booking.setCurrentStatus(currentStatus);

		// Delete Booking from TrackingQueue if status is delivered
		if (EdiStatus.equals("D1"))
			bookRepo.deleteFromTrackingQueue(id);

		bookRepo.save(booking);
		bookRepo.refresh(booking);
	}
}
