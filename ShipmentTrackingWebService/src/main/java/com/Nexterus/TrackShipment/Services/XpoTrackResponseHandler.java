package com.Nexterus.TrackShipment.Services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.BookingStatus;

@Service
public class XpoTrackResponseHandler {

	public BookingStatus handleXpoTrackResponse(JSONObject json) {
		
		String status = null;
		String message = null;
		String location = null;
		java.sql.Timestamp timestamp = null;
		try {
			JSONArray jArr = new JSONArray();
			jArr = json.getJSONObject("data").getJSONArray("shipmentStatusDtls");
			status = jArr.getJSONObject(0).getJSONObject("shipmentStatus").getString("statusCd");
			message = jArr.getJSONObject(0).getJSONObject("shipmentStatus").getString("description");
			try {
			location = jArr.getJSONObject(0).getJSONObject("currSic").getString("sicName");
			}
			 catch (JSONException e) {
					System.err.println(e.getCause() + " " + e.getMessage());
					try {
						location = jArr.getJSONObject(0).getJSONObject("origSic").getString("sicName");
						}
					catch(JSONException e1) {
						System.err.println(e1.getCause() + " " + e1.getMessage());
					}
				}
			Long time = json.getLong("transactionTimestamp");
			timestamp = new java.sql.Timestamp(time);
			System.out.println(timestamp);
		} catch (JSONException e) {
			System.err.println(e.getCause() + " " + e.getMessage());
			return null;
		}
		BookingStatus bookingStatus = new BookingStatus();
		bookingStatus.setStatus(status);
		bookingStatus.setLocation(location);
		bookingStatus.setMessage(message);
		bookingStatus.setDate(timestamp);
		return bookingStatus;
	}
	
	public java.sql.Timestamp getPickupDate(JSONObject json){
		java.sql.Timestamp pkupDate = null;
		try {
			JSONArray jArr = new JSONArray();
			jArr = json.getJSONObject("data").getJSONArray("shipmentStatusDtls");
			pkupDate = new java.sql.Timestamp(jArr.getJSONObject(0).getLong("pkupDate"));
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
			return null;
		}
		return pkupDate;
	}
}
