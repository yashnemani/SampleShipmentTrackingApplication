package com.Nexterus.TrackShipment.Services;

import java.math.BigDecimal;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingCurrentStatus;
import com.Nexterus.TrackShipment.Entities.BookingReferences;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Entities.NxtStatusDates;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;

@Service
public class BanyanStatusHandlerService {

	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;

	public void handleLoadStatus(JSONObject statusResponse) {
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
			/*String dt = statusResponse.getString("DateTime");*/
			String dt = "2018-05-10T17:06:13.050+0000";
			String date = dt.substring(0, 10);
			String time = dt.substring(11, 19);
			System.out.println("Date: " + date + " Time: " + time);
			DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.sql.Timestamp dateTime = null;
			try {
				dateTime = new java.sql.Timestamp(dateTimeFormatter.parse(date + " " + time).getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}

			List<BigDecimal> bookingIds = new ArrayList<>();
			bookingIds = bookStatusRepo.findBookingByReference(3, loadId.toString());
			if (bookingIds.isEmpty())
				return;
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
			} else if (!reff1.get().getReference().equals(proNum)) {
				BookingReferences bookref = reff1.get();
				bookref.setReference(reff1.get().getReference());
				references.add(bookref);
			}

			Optional<BookingReferences> reff2 = refStreamSupplier.get().filter(a -> a.getRef_type() == 1).findAny();
			if (!reff2.isPresent()) {
				BookingReferences bookref = new BookingReferences();
				bookref.setBooking(booking);
				bookref.setRef_type(1);
				bookref.setReference(bolNum);
				references.add(bookref);
			} else if (!reff2.get().getReference().equals(proNum)) {
				BookingReferences bookref = reff2.get();
				bookref.setReference(reff2.get().getReference());
				references.add(bookref);
			}
			booking.setReferences(references);
			
			NxtStatusDates statusDates = new NxtStatusDates();
			if (booking.getStatusDates() != null)
				statusDates = booking.getStatusDates();
			else
				statusDates.setBooking(booking);
			booking.setStatusDates(statusDates);
			String EdiStatus = status;
			String NxtStatus = bookStatusRepo.findNxtStatus(EdiStatus);
			if (NxtStatus == null) {
System.err.println("EDI Status "+EdiStatus+" does not exist in DB or does not have a valid mapping!");
return;
			}
			if (NxtStatus.equals("DL"))
				statusDates.setDt_delivered(dateTime);
			else if (NxtStatus.equals("IT")) {
				if (statusDates.getDt_pickedup() == null)
					statusDates.setDt_pickedup(dateTime);
			}
			
			Set<BookingStatus> bookStatuses = new HashSet<>();
			BookingStatus bookingStatus = new BookingStatus();
			bookingStatus.setLocation(location);
			bookingStatus.setMessage(message);
			bookingStatus.setDate(dateTime);
			bookingStatus.setStatus(EdiStatus);
			bookingStatus.setBooking(booking);
			bookStatuses.add(bookingStatus);
			booking.setStatuses(bookStatuses);

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
			booking.setCurrentStatus(currentStatus);

			bookRepo.save(booking);
			bookRepo.refresh(booking);
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
			return;
		}
	}
}
