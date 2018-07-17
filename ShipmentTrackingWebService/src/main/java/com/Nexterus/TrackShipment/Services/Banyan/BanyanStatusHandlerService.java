package com.Nexterus.TrackShipment.Services.Banyan;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.json.JSONException;
import org.json.JSONObject;
import org.pmw.tinylog.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingCurrentStatus;
import com.Nexterus.TrackShipment.Entities.BookingReferences;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Entities.NxtStatusDates;
import com.Nexterus.TrackShipment.Models.Banyan.BanyanStatus;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;
import com.google.gson.Gson;

@Service
public class BanyanStatusHandlerService {

	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	BuildTrackingStatusJson buildService;

	@Transactional
	public void handleLoadStatus(BanyanStatus banStatus) {

		Gson gson = new Gson();
		String js = gson.toJson(banStatus);
		JSONObject statusResponse = new JSONObject();

		try {
			statusResponse = new JSONObject(js);
		} catch (JSONException e) {
			System.err.println(e.getCause().getMessage());
			return;
		}

		try {
			Integer loadId = statusResponse.getInt("LoadID");
			String bolNum = statusResponse.getString("BOL");
			String proNum = statusResponse.getString("ProNumber");
			String city = null;
			String state = null;
			String location = null;
			if (statusResponse.has("City"))
				city = statusResponse.getString("City");
			if (statusResponse.has("State"))
				state = statusResponse.getString("State");
			if (city != null && state != null)
				location = city + "," + state;
			String message = statusResponse.getString("CarrierMessage");
			String status = statusResponse.getString("Code");

			List<BigDecimal> bookingIds = new ArrayList<>();
			bookingIds = bookStatusRepo.findBookingByReference(3, loadId.toString());
			if (bookingIds.isEmpty()) {
				System.out.println("No Booking found with the following Load ID" + loadId + " as Reference");
				Logger.warn("No Booking found with the following Load ID" + loadId + " as Reference");
				return;
			}
			System.out.println(bookingIds.get(0));
			Integer bookingID = bookingIds.get(0).intValue();
			Booking booking = bookRepo.getOne(bookingID);
			Set<BookingReferences> references = new HashSet<>();

			Supplier<Stream<BookingReferences>> refStreamSupplier = () -> booking.getReferences().stream();
			Optional<BookingReferences> reff1 = refStreamSupplier.get().filter(a -> a.getRef_type() == 0).findAny();
			if (!reff1.isPresent()) {
				BookingReferences bookref = new BookingReferences();
				bookref.setBooking(booking);
				bookref.setRef_type(0);
				bookref.setReference(proNum);
				references.add(bookref);
			} else if (reff1.get().getReference() == null || !reff1.get().getReference().equals(proNum)) {
				BookingReferences bookref = reff1.get();
				bookref.setReference(proNum);
				references.add(bookref);
			}

			Optional<BookingReferences> reff2 = refStreamSupplier.get().filter(a -> a.getRef_type() == 1).findAny();
			if (!reff2.isPresent()) {
				BookingReferences bookref = new BookingReferences();
				bookref.setBooking(booking);
				bookref.setRef_type(1);
				bookref.setReference(bolNum);
				references.add(bookref);
			} else if (reff2.get().getReference() == null || !reff2.get().getReference().equals(bolNum)) {
				BookingReferences bookref = reff2.get();
				bookref.setReference(bolNum);
				references.add(bookref);
			}
			booking.setReferences(references);

			String EdiStatus = status;
			String NxtStatus = bookStatusRepo.findNxtStatus(EdiStatus);
			String scac = null;
			if (NxtStatus == null) {
				System.err
						.println("EDI Status " + EdiStatus + " does not exist in DB or does not have a valid mapping!");
				/* scac = bookRepo.getSCAC(bookingID); */
				Logger.warn("EDI Status " + EdiStatus + " Missing! RateQuote: " + bookingID + " and SCAC: " + scac);
				return;
			}

			String dt = null;
			java.sql.Timestamp dateTime = null;
			if (banStatus.getDateTime() != null) {
				dt = banStatus.getDateTime().toString();
				String date = dt.substring(0, 10);
				String time = dt.substring(11, 19);
				System.out.println("Date: " + date + " Time: " + time);
				DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					dateTime = new java.sql.Timestamp(dateTimeFormatter.parse(date + " " + time).getTime());
				} catch (ParseException e) {
					System.err.println(e.getCause() + " " + e.getMessage());
				}

				NxtStatusDates statusDates = new NxtStatusDates();
				if (booking.getStatusDates() != null)
					statusDates = booking.getStatusDates();
				else
					statusDates.setBooking(booking);
				if (NxtStatus.equals("DL"))
					statusDates.setDt_delivered(dateTime);
				else if (NxtStatus.equals("IT")) {
					if (statusDates.getDt_pickedup() == null)
						statusDates.setDt_pickedup(dateTime);
				}
				booking.setStatusDates(statusDates);
			}

			List<BookingStatus> statuses = new ArrayList<>();
			BookingStatus bookingStatus = new BookingStatus();
			bookingStatus.setLocation(city);
			bookingStatus.setState(state);
			bookingStatus.setMessage(message);
			bookingStatus.setDate(dateTime);
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
			currentStatus.setLocation(location);
			currentStatus.setMessage(message);
			currentStatus.setStatus(bookingStatus);
			currentStatus.setShipStatus(EdiStatus);
			currentStatus.setShipState(NxtStatus);
			currentStatus.setDate(dateTime);
			currentStatus.setLastUpdatedDt();

			if (EdiStatus.equals("AG")) {
				currentStatus.setDate(new Timestamp(System.currentTimeMillis()));
				currentStatus.setEstDeliveryDt(dateTime);
			}
			booking.setCurrentStatus(currentStatus);

			// Delete Booking from TrackingQueue if status is delivered
			if (EdiStatus.equals("D1"))
				bookRepo.deleteFromTrackingQueue(bookingID);

			try {
				bookRepo.save(booking);
			} catch (Exception e) {
				System.err.println("Exception " + e.getMessage() + " " + e.getStackTrace());
				Logger.error("Exception " + e.getMessage() + " " + e.getStackTrace());
			}
			try {
				if (!statuses.isEmpty()) {
					scac = bookRepo.getSCAC(bookingID);
					buildService.updateTrackingStatuses(scac, bookingID.toString(), proNum, statuses);
				}
			} catch (Exception e) {
				System.out.println("Exception " + e.getMessage() + " " + e.getStackTrace());
				Logger.error("Exception " + e.getMessage() + " " + e.getStackTrace());
			}

		} catch (JSONException e) {
			System.err.println(e.getCause() + " " + e.getMessage());
			return;
		}
	}
}
