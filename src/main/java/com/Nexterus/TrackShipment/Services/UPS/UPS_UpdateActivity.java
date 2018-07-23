package com.Nexterus.TrackShipment.Services.UPS;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
public class UPS_UpdateActivity {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(UPS_UpdateActivity.class);
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
	public void updateActivity(Object obj, Integer id, int provider, String pro) {

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
		if (booking.getStatusDates() != null)
			statusDates = booking.getStatusDates();
		else
			statusDates.setBooking(booking);

		try {
			jobj = new JSONObject(json);
			if (jobj.has("Fault")) {
				log.error("UPS FAULT!!");
				try {
					String error = jobj.getJSONObject("Fault").getJSONObject("detail").getJSONObject("Errors")
							.getJSONObject("ErrorDetail").getJSONObject("PrimaryErrorCode").getString("Description");
					log.error("UPS Tracking Exception for " + id + " Error:" + error);
				} catch (JSONException e) {
					log.error("JSON Exception " + e.getMessage());
				}
				return;
			} else {
				JSONObject jsObj = new JSONObject();
				JSONObject js = new JSONObject();
				jsObj = jobj.getJSONObject("TrackResponse").getJSONObject("Shipment");
				JSONArray jArr = new JSONArray();
				String dt, time = null;
				Timestamp estDlvr = null;
				// Get Estimated Delivery Date
				try {
					jArr = jsObj.getJSONArray("DeliveryDetail");
					if (jArr.getJSONObject(0).getJSONObject("Type").getString("Description")
							.equals("Estimated Delivery")) {
						dt = jArr.getJSONObject(0).getString("Date");
						time = jArr.getJSONObject(0).getString("Time");
						estDlvr = parseDateTime(dt, time);
					}
				} catch (JSONException e) {
					log.error("JSON Exception " + e.getMessage());
					try {
						js = jsObj.getJSONObject("DeliveryDetail");
						if (js.getJSONObject("Type").getString("Description").equals("Estimated Delivery")) {
							dt = js.getString("Date");
							time = js.getString("Time");
							estDlvr = parseDateTime(dt, time);
						}
					} catch (JSONException e1) {
						log.error("JSON Exception " + e1.getMessage());
					}
				}

				// Get List of Activities
				try {
					jArr = jsObj.getJSONArray("Activity");
				} catch (JSONException e) {
					log.error("JSON Exception " + e.getMessage());
				}

				for (int i = jArr.length() - 1; i >= 0; i--) {
					JSONObject activity = new JSONObject();
					activity = jArr.getJSONObject(i);

					// Process each activity from list and generate a booking status
					bookingStatus = processActivity(activity, i, jArr.length() - 1);
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
					if (estDlvr != null)
						currentStatus.setEstDeliveryDt(estDlvr);
				}
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
				if(!statuses.isEmpty())
				buildService.updateTrackingStatuses("UPGF", id.toString(), pro, statuses);
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

	public BookingStatus processActivity(JSONObject json, int i, int max) {

		String status = null;
		String message = null;
		String city = null;
		String state = null;
		Timestamp timestamp = null;
		String dt = null;
		String time = null;
		JSONObject location = new JSONObject();
		BookingStatus bookingStatus = new BookingStatus();
		try {
			message = json.getString("Description");
			location = json.getJSONObject("ActivityLocation");
			city = location.getString("City");
			state = location.getString("StateProvinceCode");
			dt = json.getString("Date");
			time = json.getString("Time");
			timestamp = parseDateTime(dt, time);
			if (message.contains("picked up"))
				status = "SPU";
			else if (message.contains("Freight location")) {
				if (i >= max - 2) {
					status = "AOT";
					message = "Arrived at Origin Terminal " + city + "," + state;
				} else {
					status = "ADT";
					message = "Arrived at Destination Terminal " + city + "," + state;
				}
			} else if (message.contains("Departure")) {
				status = "P1";
				message = "Departed Terminal Location " + city + "," + state;
			} else if (message.equals("Shipment has arrived at a Service Center")) {
				status = "X4";
				message = "Arrived at Interim Terminal " + city + "," + state;
			} else if (message.contains("delivery appointment")) {
				status = "X9";
				message = "Delivery Appointment setup";
			} else if (message.equals("Out for Delivery"))
				status = "OFD";
			else if (message.contains("delivered"))
				status = "D1";
			else if (message.contains("damage"))
				status = "A9";
			else if (message.contains("partial"))
				status = "PRTR";
			else if (message.contains("delayed") || message.contains("delay"))
				status = "SD";
			else if (message.contains("cannot be found")
					|| message.contains("attempting to obtain appointment information"))
				status = "UA";
			else if (message.contains("Delivery Confirmation pending from the consignee"))
				status = "CD";
			else if (message.contains("clearing agency")) {
				if (message.contains("Released by"))
					status = "CUSTC ";
				else
					status = "CDL";
			} else {
				log.warn("Unhandled Message " + message);
			}

			bookingStatus.setDate(timestamp);
			bookingStatus.setLocation(city);
			bookingStatus.setState(state);
			bookingStatus.setMessage(message);
			bookingStatus.setStatus(status);
			return bookingStatus;

		} catch (JSONException e) {
			nxtLogger.error(e.getMessage());
			return null;
		}
	}

	private Timestamp parseDateTime(String dt, String time) {
		Timestamp timestamp = null;
		String year = dt.substring(0, 4);
		String month = dt.substring(4, 6);
		String day = dt.substring(6, 8);
		String hour = time.substring(0, 2);
		String minutes = time.substring(2, 4);
		String sec = time.substring(4, 6);
		DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			timestamp = new java.sql.Timestamp(dateTimeFormatter
					.parse(year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + sec).getTime());
			return timestamp;
		} catch (ParseException e) {
			nxtLogger.error(e.getStackTrace().toString());
			return null;
		}
	}
}
