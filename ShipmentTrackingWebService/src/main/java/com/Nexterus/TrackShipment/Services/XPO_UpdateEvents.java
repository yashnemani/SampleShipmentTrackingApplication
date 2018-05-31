package com.Nexterus.TrackShipment.Services;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
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
public class XPO_UpdateEvents {

	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	TrackSchedulerService trackSchedulerService;

	@Transactional
	public void updateEvents(Object obj, int id, int provider) {

		String EdiStatus = null;
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		JSONObject jobj;
		Booking booking = bookRepo.getOne(id);
		BookingStatus bookingStatus = new BookingStatus();
		Set<BookingStatus> statuses = new HashSet<>();

		BookingCurrentStatus currentStatus = new BookingCurrentStatus();
		if (booking.getCurrentStatus() != null)
			currentStatus = booking.getCurrentStatus();

		NxtStatusDates statusDates = new NxtStatusDates();
		if (booking.getStatusDates() != null)
			statusDates = booking.getStatusDates();
		else
			statusDates.setBooking(booking);

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
				else
					statuses.add(bookingStatus);

				System.out.println("EDI Status: " + EdiStatus);
				if (EdiStatus.equals("SPU"))
					statusDates.setDt_pickedup(bookingStatus.getDate());
				if (EdiStatus.equals("D1"))
					statusDates.setDt_delivered(bookingStatus.getDate());

				if (i == 0) {
					String NxtStatus = null;
					NxtStatus = bookStatusRepo.findNxtStatus(EdiStatus);
					currentStatus.setBooking(booking);
					currentStatus.setLocation(bookingStatus.getLocation());
					currentStatus.setMessage(bookingStatus.getMessage());
					currentStatus.setStatus(bookingStatus);
					currentStatus.setShipStatus(EdiStatus);
					currentStatus.setShipState(NxtStatus);
					currentStatus.setDate(bookingStatus.getDate());
					/*
					 * if (estDlvr != null) currentStatus.setEstDeliveryDt(estDlvr);
					 */
				}
			}
			if (statusDates != null)
				booking.setStatusDates(statusDates);
			booking.setStatuses(statuses);
			if (currentStatus.getBooking() != null) {
				if (currentStatus.getLastUpdatedDt() == null) {
					System.out.println(currentStatus.getBooking().getBooking_id());
					currentStatus.setLastUpdatedDt();
					booking.setCurrentStatus(currentStatus);
				}
			}
			try {
				bookRepo.save(booking);
				bookRepo.refresh(booking);
			} catch (Exception ex) {
				System.err.println(ex.getCause().getMessage());
			}

			if (EdiStatus != null) {
				if (EdiStatus.equals("D1") || EdiStatus.equals("CA")) {
					trackSchedulerService.trackDeliveredCount(provider);
					bookRepo.deleteFromTrackingQueue(id);
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
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
			if (message.equals("Reported picked up")) {
				status = "SPU";
			}
			if (message.equals("Loaded on trailer")) {
				if (i < 3) {
					status = "CP";
					message = "Completed Loading at Pickup Location";
				}
			}
			if (message.equals("City arrived")) {
				status = "AOT";
				message = "Arrived at Origin Terminal " + location;
			}
			if (message.equals("Schedule dispatched")) {
				status = "P1";
				message = "Departed Terminal location " + location;
			}
			if (message.equals("Schedule arrived")) {
				status = "X4";
				message = "Arrived at Terminal location " + location;
			}
			if (message.equals("Closed for delivery")) {
				status = "ADT";
				message = "Arrived at Delivery Terminal location " + location;
			}
			if (message.contains("Out for delivery"))
				status = "OFD";
			if (message.equals("Arrived at Customer"))
				status = "X1";
			if (message.equals("Final delivered")) {
				status = "D1";
				message = "Delivered to Consignee " + location;
			}

			BookingStatus bookingStatus = new BookingStatus();
			bookingStatus.setDate(timestamp);
			bookingStatus.setLocation(location);
			bookingStatus.setMessage(message);
			bookingStatus.setStatus(status);
			return bookingStatus;
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
			return null;
		}
	}
}
