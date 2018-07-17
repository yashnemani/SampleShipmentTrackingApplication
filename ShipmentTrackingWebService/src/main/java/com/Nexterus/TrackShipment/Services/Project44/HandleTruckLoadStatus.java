package com.Nexterus.TrackShipment.Services.Project44;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingCurrentStatus;
import com.Nexterus.TrackShipment.Entities.BookingStatus;
import com.Nexterus.TrackShipment.Entities.NxtStatusDates;
import com.Nexterus.TrackShipment.Models.Project44.ShipmentStops;
import com.Nexterus.TrackShipment.Models.Project44.StatusUpdate;
import com.Nexterus.TrackShipment.Models.Project44.StopStatus;
import com.Nexterus.TrackShipment.Models.Project44.TrackLoadStatusResponse;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;
import com.Nexterus.TrackShipment.Services.Banyan.BuildTrackingStatusJson;

@Service
public class HandleTruckLoadStatus {

	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	BuildTrackingStatusJson buildService;

	@Transactional
	public void handle_TL_Status(TrackLoadStatusResponse trackResponse) {

		String location = null;
		Timestamp date = null;
		LocalDateTime locDate = null;
		if (trackResponse == null)
			return;
		List<ShipmentStops> stops = new ArrayList<>();
	/*	List<StatusUpdate> statusUpdates = new ArrayList<>();*/
		List<StopStatus> stopStatuses = new ArrayList<>();
		StatusUpdate latestStatusUpdate = new StatusUpdate();
		latestStatusUpdate = trackResponse.getLatestStatusUpdate();
		/*statusUpdates = trackResponse.getStatusUpdates();*/
		stopStatuses = trackResponse.getLatestStopStatuses();
		stops = trackResponse.getShipment().getShipmentStops();

		// Get BookingId using the Project44 ID
		Integer bookingId = bookStatusRepo.findBookingByReference(3, trackResponse.getShipment().getId().toString())
				.get(0).intValue();

		Booking booking = new Booking();
		booking = bookRepo.getOne(bookingId);
		List<BookingStatus> statuses = new ArrayList<>();
		if (booking.getStatuses() != null)
			statuses = booking.getStatuses();

		if (stopStatuses != null) {
			for (int i = 0; i < stopStatuses.size(); i++) {
				BookingStatus bookingStatus = new BookingStatus();
				bookingStatus.setBooking(booking);
				String edi = bookStatusRepo.findEdiStatus(3, stopStatuses.get(i).getStatusCode());
				if (edi == null) {
					System.out.println("No valid Mapping for status code " + stopStatuses.get(i).getStatusCode());
					continue;
				}

				// Check for Duplicates and add Status
				String loc = stops.get(stopStatuses.get(i).getStopNumber() - 1).getLocation().getAddress().getCity();
				if (!statuses.isEmpty()) {
					if (loc != null) {
						if (statuses.stream().filter(a -> a.getStatus().equals(edi) && a.getLocation() != null
								&& a.getLocation().equals(loc)).findAny().isPresent())
							continue;
						else
							statuses.add(bookingStatus);
					} else {
						if (statuses.stream().filter(a -> a.getStatus().equals(edi) && a.getLocation() == null)
								.findAny().isPresent())
							continue;
						else
							statuses.add(bookingStatus);
					}
				} else
					statuses.add(bookingStatus);

				bookingStatus.setStatus(edi);
				bookingStatus.setLocation(loc);
				bookingStatus.setState(
						stops.get(stopStatuses.get(i).getStopNumber() - 1).getLocation().getAddress().getState());
				String message = stopStatuses.get(i).getArrivalCode() + " " + stopStatuses.get(i).getArrivalDateTime()
						+ " Stop Number: " + stopStatuses.get(i).getStopNumber();
				bookingStatus.setMessage(message);
				if (stopStatuses.get(i).getArrivalEstimate() != null)
					locDate = LocalDateTime.parse(stopStatuses.get(i).getArrivalEstimate().getLastCalculatedDateTime());

				if (locDate == null)
					date = null;
				else
					date = Timestamp.valueOf(locDate);
				bookingStatus.setDate(date);
				statuses.add(bookingStatus);
			}
		}

		// Set Status Dates
		if (stopStatuses.size() > 0) {
			NxtStatusDates statusDates = new NxtStatusDates();
			if (booking.getStatusDates() != null)
				statusDates = booking.getStatusDates();
			else
				statusDates.setBooking(booking);

			if (statusDates.getDt_pickedup() == null) {
				// Set Date_PickedUp
				if (stopStatuses.get(0).getDepartureDateTime() != null) {
					String pickupTime = getTimeString(stopStatuses.get(0).getDepartureDateTime());
					locDate = LocalDateTime.parse(pickupTime);
					date = Timestamp.valueOf(locDate);
					statusDates.setDt_pickedup(date);
				} else if (stopStatuses.size() > 1) {
					if (stopStatuses.get(1).getArrivalDateTime() != null) {
						String pickupTime = getTimeString(stopStatuses.get(1).getArrivalDateTime());
						locDate = LocalDateTime.parse(pickupTime);
						date = Timestamp.valueOf(locDate);
						statusDates.setDt_pickedup(date);
					} else {
						System.out.println("Could not set PickedUp Date!");
					}
				}
			}

			if (statusDates.getDt_delivered() == null) {
				// Set Date_Delivered
				if (stopStatuses.get(stopStatuses.size() - 1).getDepartureDateTime() != null) {
					String pickupTime = getTimeString(stopStatuses.get(stopStatuses.size() - 1).getDepartureDateTime());
					locDate = LocalDateTime.parse(pickupTime);
					date = Timestamp.valueOf(locDate);
					statusDates.setDt_delivered(date);
				}
			}
			booking.setStatusDates(statusDates);
		}

		// Set Booking Current Status
		BookingCurrentStatus currentStatus = new BookingCurrentStatus();
		if (booking.getCurrentStatus() != null)
			currentStatus = booking.getCurrentStatus();
		else
			currentStatus.setBooking(booking);
		BookingStatus bookingStatus = new BookingStatus();
		bookingStatus.setBooking(booking);
		if (latestStatusUpdate.getAddress() != null) {
			currentStatus.setLocation(latestStatusUpdate.getAddress().getCity());
			bookingStatus.setLocation(latestStatusUpdate.getAddress().getCity());
			currentStatus.setState(latestStatusUpdate.getAddress().getState());
			bookingStatus.setState(latestStatusUpdate.getAddress().getState());
			location = latestStatusUpdate.getAddress().getCity();
		}
		String ediStatus = bookStatusRepo.findEdiStatus(3, latestStatusUpdate.getStatusCode());
		if (ediStatus == null) {
			System.out.println("No valid Mapping for status code " + latestStatusUpdate.getStatusCode());
			return;
		}
		currentStatus.setShipStatus(ediStatus);
		bookingStatus.setStatus(ediStatus);
		String NxtStatus = bookStatusRepo.findNxtStatus(ediStatus);
		currentStatus.setShipState(NxtStatus);
		String msg = latestStatusUpdate.getStatusReason().getDescription();
		currentStatus.setMessage(msg);
		bookingStatus.setMessage(msg);
		String timestamp = getTimeString(latestStatusUpdate.getTimestamp());
		System.out.println("Timestamp Substring " + timestamp);
		locDate = LocalDateTime.parse(timestamp);
		date = Timestamp.valueOf(locDate);
		currentStatus.setDate(date);
		bookingStatus.setDate(date);

		// Check for Duplicates and add Status
		if (!statuses.isEmpty()) {
			if (location != null) {
				String loca = location;
				if (!statuses.stream().filter(
						a -> a.getStatus().equals(ediStatus) && a.getLocation() != null && a.getLocation().equals(loca))
						.findAny().isPresent())
					statuses.add(bookingStatus);
			} else {
				if (!statuses.stream().filter(a -> a.getStatus().equals(ediStatus) && a.getLocation() == null).findAny()
						.isPresent())
					statuses.add(bookingStatus);
			}
		} else
			statuses.add(bookingStatus);

		currentStatus.setStatus(bookingStatus);
		booking.setStatuses(statuses);
		booking.setCurrentStatus(currentStatus);
		bookRepo.save(booking);
		bookRepo.refresh(booking);
		
		// Update Utl_Status Package
/*		try {
			String proNo = null;
			if (!trackResponse.getShipment().getAttributes().isEmpty()) {
				if (trackResponse.getShipment().getAttributes().stream().filter(a -> a.getname().equals("PRO"))
						.findFirst().isPresent())
					proNo = trackResponse.getShipment().getAttributes().stream().filter(a -> a.getname().equals("PRO"))
							.findFirst().get().getValue();
			}
			buildService.updateTrackingStatuses(booking.getCARRIER_CODE(), bookingId.toString(), proNo, statuses);
		} catch (Exception e) {
			System.out.println("Exception :" + e.getMessage());
			Logger.error("Exception :" + e.getMessage() + " " + e.getStackTrace());
		}*/
	}

	private String getTimeString(String time) {
		return time.substring(0, 19);
	}
}
