package com.Nexterus.TrackShipment.Services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.Timestamp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.BanyanTrackingResponse;
import com.Nexterus.TrackShipment.Repos.BanyanTrackingResponseRepository;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Repos.BookingStatusRepository;
import com.google.gson.Gson;

@Service
public class BanyanTrackResponseHandler {

	@Autowired
	BookingStatusRepository bookStatusRepo;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	SampleBanyanTrackResponse sampleService;
	@Autowired
	BanyanStatusHandlerService statusHandlerService;
	@Autowired
	BanyanTrackingResponseRepository saveResponseRepo;

	public void handleTrackResponse(JSONObject json) {

		try {
			if (json.getBoolean("Success") == false) {
				System.out.println("No trackng statuses returned by Banyan");
				return;
			}
			JSONArray statuses = null;
			Gson gson = new Gson();

			if (!json.has("TrackingStatuses")) {
				System.out.println("No trackng statuses returned by Banyan");
				// Test Response
			/*	String jstring = gson.toJson(sampleService.getSampleBanyanTrackresponse());*/
			/*	json = new JSONObject(jstring);*/
				return;
			}
			statuses = json.getJSONArray("TrackingStatuses");
			Object obj = gson.fromJson(statuses.toString(), Object.class);
			saveBanyanTrackResponse(obj);

			for (int i = statuses.length() - 1; i >= 0; i--) {
				JSONObject statusResponse = statuses.getJSONObject(i);
				System.out.println(i + " " + statusResponse.getString("Code"));
				statusHandlerService.handleLoadStatus(statusResponse);
			}
		} catch (JSONException e) {
			System.out.println(e.getCause() + " " + e.getMessage());
			return;
		}
		return;
	}

	public Object getBanyanResponse(int id) {

		if (!saveResponseRepo.existsById(id)) {
			System.out.println("No saved response with given ID");
			return null;
		}
		BanyanTrackingResponse banyanTrackResponse = new BanyanTrackingResponse();
		banyanTrackResponse = saveResponseRepo.findById(id).get();
		Object obj = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(banyanTrackResponse.getTrackResponse());
			ObjectInputStream his = new ObjectInputStream(in);
			obj = his.readObject();
			return obj;
		} catch (ClassNotFoundException | IOException e) {
			System.err.println("Deserialize Track Response " + e.getCause().getMessage());
		}
		return obj;
	}

	public void saveBanyanTrackResponse(Object obj) {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			try {
				out = new ObjectOutputStream(bos);
				out.writeObject(obj);
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] yourBytes = bos.toByteArray();
			BanyanTrackingResponse banyanTrackResponse = new BanyanTrackingResponse();
			banyanTrackResponse.setTrackResponse(yourBytes);
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			banyanTrackResponse.setTimestamp(timestamp);
			saveResponseRepo.save(banyanTrackResponse);
		} finally {
			try {
				out.close();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
