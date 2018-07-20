package com.Nexterus.TrackShipment.Services.General;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Repos.BookingRepository;

@Service
public class GetCurrentStatus {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(GetCurrentStatus.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	
	@Autowired
	BookingRepository bookRepo;

	public JSONObject getBookingCurrentStatus(int bookingID) {
		
		Booking book = null;
		if (bookRepo.existsById(bookingID))
			book = bookRepo.findById(bookingID).get();
		else
			return null;

		JSONObject json = new JSONObject();
		if (book.getCurrentStatus() != null) {
			try {
				// Current Status Details
				json.put("Shipment Status", book.getCurrentStatus().getShipStatus());
				json.put("Shipment State", book.getCurrentStatus().getShipState());
				json.put("Nexterus Message", book.getCurrentStatus().getMessage());
				json.put("Current Location", book.getCurrentStatus().getLocation());
				json.put("Date Updated", book.getCurrentStatus().getDate().toString());

				// Booking References
				if (!book.getReferences().isEmpty()) {
					JSONObject references = new JSONObject();
					if (book.getReferences().stream().filter(a -> a.getRef_type() == 0).findFirst().isPresent())
						references.put("Shipment ManifestID", book.getReferences().stream()
								.filter(a -> a.getRef_type() == 0).findFirst().get().getReference());
					if (book.getReferences().stream().filter(a -> a.getRef_type() == 1).findFirst().isPresent())
						references.put("Shipment BOL_Number", book.getReferences().stream()
								.filter(a -> a.getRef_type() == 1).findFirst().get().getReference());
					json.put("Shipment References", references);
				}

				else {
					log.warn("Booking with ID: " + bookingID + " does not have any References");
				}

				// Nexterus Status Dates
				if (book.getStatusDates() != null) {
					JSONObject StatusDates = new JSONObject();
					if (book.getStatusDates().getDt_entered() != null)
						StatusDates.put("Shipment Created", book.getStatusDates().getDt_entered().toString());
					if (book.getStatusDates().getDt_pickedup() != null)
						StatusDates.put("Shipment PickedUp", book.getStatusDates().getDt_pickedup().toString());
					if (book.getStatusDates().getDt_delivered() != null)
						StatusDates.put("Shipment Delievered", book.getStatusDates().getDt_delivered().toString());
					json.put("Status Dates", StatusDates);

				} else {
					log.warn("Booking with ID: " + bookingID + " does not have Nexterus Status Dates");
				}
				return json;
			} catch (JSONException e) {
				nxtLogger.error(e.getStackTrace().toString());
			}
		}
		return null;
	}
}
