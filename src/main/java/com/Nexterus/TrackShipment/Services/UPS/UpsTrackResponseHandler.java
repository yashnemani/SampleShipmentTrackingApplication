package com.Nexterus.TrackShipment.Services.UPS;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.BookingStatus;

@Service
public class UpsTrackResponseHandler {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(UpsTrackResponseHandler.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	
	String pro = null;
	Timestamp estDlvr = null;

	public BookingStatus handleTrackResponse(JSONObject json) {

		String status = null;
		String message = null;
		String location = null;
		Timestamp timestamp = null;

		try {
			if (json.has("Fault")) {
				nxtLogger.error("UPS FAULT!!");
				try {
					String error = json.getJSONObject("Fault").getJSONObject("detail").getJSONObject("Errors")
							.getJSONObject("ErrorDetail").getJSONObject("PrimaryErrorCode").getString("Description");
					nxtLogger.error(error);
				} catch (JSONException e) {
					nxtLogger.error(e.getCause() + " " + e.getMessage());
				}
				return null;
			}
			JSONObject jsob;
			jsob = json.getJSONObject("TrackResponse").getJSONObject("Shipment");
			try {
				pro = jsob.getJSONObject("InquiryNumber").getString("Value");
			} catch (JSONException e) {
				nxtLogger.error(e.getCause() + " " + e.getMessage());
			}
			status = jsob.getJSONObject("CurrentStatus").getString("Code");
			try {
				location = jsob.getJSONObject("ShipmentAddress").getJSONObject("Address").getString("PostalCode");
			} catch (JSONException e) {
				nxtLogger.error(e.getCause() + " " + e.getMessage());
			}
			message = jsob.getJSONObject("CurrentStatus").getString("Description");

			JSONArray jArr = new JSONArray();
			JSONObject jsObj;
			String dt = null;
			String time = null;
			try {
				jArr = jsob.getJSONArray("DeliveryDetail");
				if (jArr.getJSONObject(0).getJSONObject("Type").getString("Description").equals("Estimated Delivery")) {
					dt = jArr.getJSONObject(0).getString("Date");
					time = jArr.getJSONObject(0).getString("Time");
				}
			} catch (JSONException e) {
				nxtLogger.error(e.getCause() + " " + e.getMessage());
				try {
					jsObj = jsob.getJSONObject("DeliveryDetail");
					if (jsObj.getJSONObject("Type").getString("Description").equals("Estimated Delivery")) {
						dt = jsObj.getString("Date");
						time = jsObj.getString("Time");
					}
				} catch (JSONException e1) {
					nxtLogger.error(e1.getCause() + " " + e1.getMessage());
				}
			}

			if (dt != null) {
				String year = dt.substring(0, 4);
				String month = dt.substring(4, 6);
				String day = dt.substring(6, 8);
				String hour = time.substring(0, 2);
				String minutes = time.substring(2, 4);
				String sec = time.substring(4, 6);
				DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				try {
					estDlvr = new java.sql.Timestamp(dateTimeFormatter
							.parse(year + "-" + month + "-" + day + " " + hour + ":" + minutes + ":" + sec).getTime());
				} catch (ParseException e) {
					nxtLogger.error(e.getStackTrace().toString());
				}
			}
			dt = null;
			try {
				jArr = jsob.getJSONArray("Activity");
				dt = jArr.getJSONObject(0).getString("Date");
				time = jArr.getJSONObject(0).getString("Time");
				message = jArr.getJSONObject(0).getString("Description");
			} catch (JSONException e) {
				nxtLogger.error(e.getCause() + " " + e.getMessage());
				try {
					jsObj = jsob.getJSONObject("Activity");
					dt = jsObj.getString("Date");
					time = jsObj.getString("Time");
					message = jsObj.getString("Description");
				} catch (JSONException e1) {
					nxtLogger.error(e1.getCause() + " " + e1.getMessage());
				}
			}

			if (dt != null) {
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
				} catch (ParseException e) {
					nxtLogger.error(e.getStackTrace().toString());
				}
			}
		} catch (JSONException e) {
			nxtLogger.error(e.getCause() + " " + e.getMessage());
			return null;
		}
		BookingStatus bookingStatus = new BookingStatus();
		bookingStatus.setStatus(status);
		bookingStatus.setLocation(location);
		bookingStatus.setMessage(message);
		bookingStatus.setDate(timestamp);
		return bookingStatus;
	}

	public java.sql.Timestamp getPickupDate(JSONObject json) {
		java.sql.Timestamp pkupDate = null;
		try {
			JSONObject jsob;
			jsob = json.getJSONObject("TrackResponse").getJSONObject("Shipment");
			String dt = jsob.getString("PickupDate");
			String year = dt.substring(0, 4);
			String month = dt.substring(4, 6);
			String day = dt.substring(6, 8);
			DateFormat dateTimeFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				pkupDate = new java.sql.Timestamp(
						dateTimeFormatter.parse(year + "-" + month + "-" + day + " " + "00:00:00").getTime());
			} catch (ParseException e) {
				nxtLogger.error(e.getStackTrace().toString());
			}
		} catch (JSONException e) {
			nxtLogger.error(e.getCause() + " " + e.getMessage());
			return null;
		}
		return pkupDate;
	}

	public String getPro() {
		String pro1 = pro;
		pro = null;
		return pro1;
	}

	public Timestamp getEstDelivery() {
		Timestamp estDlvr1 = estDlvr;
		estDlvr = null;
		return estDlvr1;
	}
}
