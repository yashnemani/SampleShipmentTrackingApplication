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

		Timestamp date = null;
		LocalDateTime locDate = null;
		if (trackResponse == null)
			return;
		List<ShipmentStops> stops = new ArrayList<>();
		List<StopStatus> stopStatuses = new ArrayList<>();
		stopStatuses = trackResponse.getLatestStopStatuses();
		stops = trackResponse.getShipment().getShipmentStops();

		// Get BookingId using the Project44 ID Reference
		Integer bookingId = bookStatusRepo.findBookingByReference(3, trackResponse.getShipment().getId().toString())
				.get(0).intValue();

		Booking booking = new Booking();
		booking = bookRepo.getOne(bookingId);

		List<BookingStatus> statuses = new ArrayList<>();
		if (booking.getStatuses() != null)
			statuses = booking.getStatuses();

		BookingCurrentStatus currentStatus = new BookingCurrentStatus();
		if (booking.getCurrentStatus() != null)
			currentStatus = booking.getCurrentStatus();
		else
			currentStatus.setBooking(booking);

		NxtStatusDates statusDates = new NxtStatusDates();
		if (booking.getStatusDates() != null)
			statusDates = booking.getStatusDates();
		else
			statusDates.setBooking(booking);

		// Get List of Stop Statuses and update Booking Entities in DB
		if (stopStatuses != null) {
			for (int i = 0; i < stopStatuses.size(); i++) {
				BookingStatus bookingStatus = new BookingStatus();
				bookingStatus.setBooking(booking);
				String edi = bookStatusRepo.findEdiStatus(3, stopStatuses.get(i).getStatusCode());
				if (edi == null) {
					System.out.println("No valid Mapping for status code " + stopStatuses.get(i).getStatusCode());
					continue;
				}
				String loc = stops.get(stopStatuses.get(i).getStopNumber() - 1).getLocation().getAddress().getCity();

				// Check for Duplicates and add Status
				boolean duplicate = true;
				if (!statuses.isEmpty()) {
					if (loc != null) {
						if (statuses.stream().filter(a -> a.getStatus().equals(edi) && a.getLocation() != null
								&& a.getLocation().equals(loc)).findAny().isPresent())
							continue;
						else
							duplicate = false;
					} else {
						if (statuses.stream().filter(a -> a.getStatus().equals(edi) && a.getLocation() == null)
								.findAny().isPresent())
							continue;
						else
							duplicate = false;
					}
				} else
					duplicate = false;

				// Add to BookingStatus List if not duplicate
				if (!duplicate) {
					bookingStatus.setStatus(edi);
					bookingStatus.setLocation(loc);
					bookingStatus.setState(
							stops.get(stopStatuses.get(i).getStopNumber() - 1).getLocation().getAddress().getState());
					String message = stopStatuses.get(i).getArrivalCode() + " "
							+ stopStatuses.get(i).getArrivalDateTime() + " Stop Number: "
							+ stopStatuses.get(i).getStopNumber();
					bookingStatus.setMessage(message);

					String timestamp = null;
					if (stopStatuses.get(i).getDepartureDateTime() != null)
						timestamp = getTimeString(stopStatuses.get(i).getDepartureDateTime());
					else
						timestamp = getTimeString(stopStatuses.get(i).getArrivalDateTime());

					locDate = LocalDateTime.parse(timestamp);
					date = Timestamp.valueOf(locDate);
					bookingStatus.setDate(date);

					statuses.add(bookingStatus);
				}

				// Set Booking CurrentStatus
				String Nxt = bookStatusRepo.findNxtStatus(edi);
				currentStatus.setBooking(booking);
				currentStatus.setLocation(bookingStatus.getLocation());
				currentStatus.setMessage(bookingStatus.getMessage());
				currentStatus.setStatus(bookingStatus);
				currentStatus.setShipStatus(edi);
				currentStatus.setShipState(Nxt);
				currentStatus.setState(bookingStatus.getState());
				currentStatus.setDate(bookingStatus.getDate());
			}
		}

		// Set Status Dates
		if (stopStatuses.size() > 0) {
			if (statusDates.getDt_pickedup() == null) {
				// Set Date_PickedUp
				String pickupTime = null;
				if (stopStatuses.get(0).getDepartureDateTime() != null) {
					pickupTime = getTimeString(stopStatuses.get(0).getDepartureDateTime());
				} else if (stopStatuses.size() > 1) {
					if (stopStatuses.get(1).getArrivalDateTime() != null) {
						pickupTime = getTimeString(stopStatuses.get(1).getArrivalDateTime());
					} else {
						System.out.println("Could not set PickedUp Date!");
					}
					if (pickupTime != null) {
						locDate = LocalDateTime.parse(pickupTime);
						date = Timestamp.valueOf(locDate);
						statusDates.setDt_pickedup(date);
					}
				}
			}

			if (statusDates.getDt_delivered() == null) {
				// Set Date_Delivered
				if (stopStatuses.get(stopStatuses.size() - 1).getDepartureDateTime() != null) {
					String delivered = getTimeString(stopStatuses.get(stopStatuses.size() - 1).getDepartureDateTime());
					locDate = LocalDateTime.parse(delivered);
					date = Timestamp.valueOf(locDate);
					statusDates.setDt_delivered(date);
				}
			}
			booking.setStatusDates(statusDates);
		}

		booking.setStatuses(statuses);
		booking.setCurrentStatus(currentStatus);
		bookRepo.save(booking);
		bookRepo.refresh(booking);
	}

	private String getTimeString(String time) {
		return time.substring(0, 19);
	}
}
