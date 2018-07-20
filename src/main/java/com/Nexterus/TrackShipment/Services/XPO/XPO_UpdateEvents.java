package com.Nexterus.TrackShipment.Services.XPO;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingCurrentStatus;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Entities.NxtStatusDates;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;
import com.Nexterus.TrackShipment.Services.Banyan.BuildTrackingStatusJson;
import com.Nexterus.TrackShipment.Services.General.TrackSchedulerService;
import com.google.gson.Gson;

@Service
public class XPO_UpdateEvents {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(XPO_UpdateEvents.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	TrackSchedulerService trackSchedulerService;
	@Autowired
	BuildTrackingStatusJson buildService;

	@Transactional
	public void updateEvents(Object obj, Integer id, int provider, String pro) {

		String EdiStatus = null;
		String NxtStatus = null;
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		JSONObject jobj;
		Booking booking = bookRepo.getOne(id);
		BookingStatus bookingStatus = new BookingStatus();
		List<BookingStatus> statuses = new ArrayList<>();

		BookingCurrentStatus currentStatus = new BookingCurrentStatus();
		if (booking.getCurrentStatus() != null)
			currentStatus = booking.getCurrentStatus();

		NxtStatusDates statusDates = new NxtStatusDates();
		if (booking.getStatusDates() != null) {
			if (booking.getStatusDates().getBooking() != null)
				statusDates = booking.getStatusDates();
			else
				statusDates.setBooking(booking);
		} else {
			statusDates.setBooking(booking);
		}

		try {
			jobj = new JSONObject(json);
			JSONObject jsonObj = new JSONObject();
			jsonObj = jobj.getJSONObject("data");
			JSONArray jArr = new JSONArray();
			jArr = jsonObj.getJSONArray("shipmentTrackingEvent");

			for (int i = 0; i < jArr.length(); i++) {
				JSONObject event = new JSONObject();
				event = jArr.getJSONObject(i);
				bookingStatus = handleEvent(event, i);
				if (bookingStatus == null)
					return;
				if (bookingStatus.getStatus() == null)
					continue;

				EdiStatus = bookingStatus.getStatus();
				final String tempStatus = bookingStatus.getStatus();
				final String tempLocation = bookingStatus.getLocation();
				bookingStatus.setBooking(booking);

				// Verify if Booking Status is already present
				if (booking.getStatuses().stream().filter(a -> a.getLocation() != null)
						.filter(a -> a.getStatus().equals(tempStatus) & a.getLocation().equals(tempLocation))
						.findFirst().isPresent())
					continue;
				else if (statuses.stream().filter(a -> a.getLocation() != null)
						.filter(a -> a.getStatus().equals(tempStatus) & a.getLocation().equals(tempLocation))
						.findFirst().isPresent())
					continue;
				else
					statuses.add(bookingStatus);

				log.info("EDI Status: " + EdiStatus);
				if (EdiStatus.equals("SPU"))
					statusDates.setDt_pickedup(bookingStatus.getDate());
				if (EdiStatus.equals("D1"))
					statusDates.setDt_delivered(bookingStatus.getDate());

				NxtStatus = bookStatusRepo.findNxtStatus(EdiStatus);
				currentStatus.setBooking(booking);
				currentStatus.setLocation(bookingStatus.getLocation());
				currentStatus.setMessage(bookingStatus.getMessage());
				currentStatus.setStatus(bookingStatus);
				currentStatus.setShipStatus(EdiStatus);
				currentStatus.setShipState(NxtStatus);
				currentStatus.setState(bookingStatus.getState());
				currentStatus.setDate(bookingStatus.getDate());
			}
			if (statusDates != null)
				booking.setStatusDates(statusDates);
			booking.setStatuses(statuses);
			if (currentStatus.getBooking() != null) {
				if (currentStatus.getLastUpdatedDt() == null) {
					log.info(currentStatus.getBooking().getBooking_id().toString());
					currentStatus.setLastUpdatedDt();
					booking.setCurrentStatus(currentStatus);
				}
			}
			try {
				bookRepo.save(booking);
				bookRepo.refresh(booking);
			} catch (Exception ex) {
				nxtLogger.error("RunTime Exception " + ex.getMessage());
			}
			try {
				if (!statuses.isEmpty()) {
					buildService.updateTrackingStatuses("CNWY", id.toString(), pro, statuses);
				}
			} catch (Exception ex) {
				nxtLogger.error("Utl_Status Exception " + ex.getMessage());
			}

			if (EdiStatus != null) {
				if (EdiStatus.equals("D1") || EdiStatus.equals("CA")) {
					trackSchedulerService.trackDeliveredCount(provider);
					bookRepo.deleteFromTrackingQueue(id);
				}
			}
		} catch (JSONException e) {
			nxtLogger.error("JSON Exception " + e.getMessage());
			return;
		}
	}

	private BookingStatus handleEvent(JSONObject j, int i) {

		JSONObject header = new JSONObject();
		JSONObject detail = new JSONObject();
		String message = null;
		Timestamp timestamp = null;
		String city = null;
		String state = null;
		String location = null;
		String status = null;
		try {
			header = j.getJSONObject("eventHdr");
			detail = j.getJSONObject("eventOccrdLoc");
			message = header.getString("eventDesc");
			timestamp = new Timestamp(header.getLong("eventTmst"));
			city = detail.getString("cityName");
			state = detail.getString("stateCd");
			location = city + " " + state;

			if (message.equals("Arrived at Customer"))
				status = "X3";
			else if (message.equals("Reported picked up")) {
				status = "SPU";
			} else if (message.equals("City arrived")) {
				status = "AOT";
				message = "Arrived at Origin Terminal " + location;
			} else if (message.equals("Schedule dispatched")) {
				status = "P1";
				message = "Departed Terminal location " + location;
			} else if (message.equals("Schedule arrived")) {
				status = "X4";
				message = "Arrived at Terminal location " + location;
			} else if (message.equals("Closed for delivery")) {
				status = "ADT";
				message = "Arrived at Delivery Terminal location " + location;
			} else if (message.contains("Out for delivery"))
				status = "OFD";
			else if (message.equals("Arrived at Customer"))
				status = "X1";
			else if (message.contains("delivered") || message.contains("Delivered")) {
				if (message.contains("short") || message.contains("shorted"))
					status = "D1SS";
				else if (message.contains("agent"))
					status = "J1";
				else
					status = "D1";
				message = message + " " + location;
			} else if (message.contains("short") || message.contains("shorted"))
				status = "SS";
			else if (message.contains("Refused"))
				status = "A7";
			else
				log.warn("Unhandled Message " + message);

			BookingStatus bookingStatus = new BookingStatus();
			bookingStatus.setDate(timestamp);
			bookingStatus.setLocation(city);
			bookingStatus.setState(state);
			bookingStatus.setMessage(message);
			bookingStatus.setStatus(status);
			return bookingStatus;
		} catch (JSONException e) {
			nxtLogger.error("JSON Exception " + e.getMessage());
			return null;
		}
	}
}
