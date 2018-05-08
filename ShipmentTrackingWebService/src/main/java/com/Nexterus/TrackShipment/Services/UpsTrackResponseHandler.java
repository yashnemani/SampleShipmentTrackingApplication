package com.Nexterus.TrackShipment.Services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.BookingStatus;

@Service
public class UpsTrackResponseHandler {

	public BookingStatus handleTrackResponse(JSONObject json) {

		String status = null;
		String message = null;
		String location = null;
		java.sql.Timestamp timestamp = null;
		try {
			if (json.has("Fault")) {
				System.out.println("UPS FAULT!!");
				return null;
			}
			JSONObject jsob;
			jsob = json.getJSONObject("TrackResponse").getJSONObject("Shipment");
			status = jsob.getJSONObject("CurrentStatus").getString("Code");
			location = jsob.getJSONObject("ShipmentAddress").getJSONObject("Address").getString("PostalCode");
			message = jsob.getJSONObject("CurrentStatus").getString("Description");

			JSONArray jArr = new JSONArray();
			JSONObject jsObj;
			String dt = null;
			String time = null;
			try {
				jArr = jsob.getJSONArray("DeliveryDetail");
				dt = jArr.getJSONObject(0).getString("Date");
				time = jArr.getJSONObject(0).getString("Time");
			} catch (JSONException e) {
				System.out.println(e.getCause() + " " + e.getMessage());
			}
			if (dt == null)
				try {
					jsObj = jsob.getJSONObject("DeliveryDetail");
					dt = jsObj.getString("Date");
					time = jsObj.getString("Time");
				} catch (JSONException e) {
					System.out.println(e.getCause() + " " + e.getMessage());
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
					System.out.println(timestamp);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
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
				System.out.println(pkupDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
			return null;
		}
		return pkupDate;
	}
}
