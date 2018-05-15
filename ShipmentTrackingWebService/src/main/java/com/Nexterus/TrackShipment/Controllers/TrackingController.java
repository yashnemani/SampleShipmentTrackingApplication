package com.Nexterus.TrackShipment.Controllers;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.Nexterus.TrackShipment.Models.Banyan.AuthenticationData;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusRequest;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusResponse;
import com.Nexterus.TrackShipment.Models.UPS.TrackRequest;
import com.Nexterus.TrackShipment.Models.UPS.UPS_TrackRequest;
import com.Nexterus.TrackShipment.Models.XPO.OAuth2Token;
import com.Nexterus.TrackShipment.Models.XPO.XPOAccess;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Services.BanyanStatusHandlerService;
import com.Nexterus.TrackShipment.Services.BanyanTrackResponseHandler;
import com.Nexterus.TrackShipment.Services.GetCurrentStatus;
import com.Nexterus.TrackShipment.Services.GetRefNum;
import com.Nexterus.TrackShipment.Services.SampleBanyanTrackResponse;
import com.Nexterus.TrackShipment.Services.TrackingResponseHandler;
import com.google.gson.Gson;

@RestController
@RequestMapping("/")
public class TrackingController {

	@Autowired
	OAuth2Token authToken;
	@Autowired
	UPS_TrackRequest UPSTrackRequest;
	@Autowired
	TrackRequest trackRequest;
	@Autowired
	TrackingResponseHandler trackResponseService;
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	GetRefNum refNumService;
	@Autowired
	GetCurrentStatus currentStatusService;
	@Autowired
	BanyanTrackResponseHandler trackResponseHandler;
	@Autowired
	SampleBanyanTrackResponse sampleService;
	@Autowired
	BanyanStatusHandlerService statusHandlerService;

	// Get a list of all updated Banyan shipment statuses
	@GetMapping("/getBanyanStatuses")
	public TrackingStatusResponse getBanyanStatuses() {

		AuthenticationData authData = new AuthenticationData();

		TrackingStatusRequest statusRequest = new TrackingStatusRequest();
		statusRequest.setAuthData(authData);

		RestTemplate restTemplate = new RestTemplate();
		String url = "http://ws.beta.banyantechnology.com/services/api/rest/GetTrackingStatuses";

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<TrackingStatusRequest> entity = new HttpEntity<>(statusRequest, headers);
		try {
			ResponseEntity<TrackingStatusResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					TrackingStatusResponse.class);
			trackResponseHandler.handleTrackResponse(response.getBody());
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			return null;
		}

	}

	// Get the Security Access Token for XPO
	@PostMapping("/getXPOBearerToken")
	public OAuth2Token getXPOBearerToken() {

		XPOAccess access = new XPOAccess();
		String tokenUrl = access.getTokenUrl();

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Authorization", access.getUserToken());

		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("grant_type", access.getGrant_type());
		map.add("username", access.getUsername());
		map.add("password", access.getPassword());

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);
		ResponseEntity<OAuth2Token> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity,
				OAuth2Token.class);

		authToken = response.getBody();

		return response.getBody();
	}

	// Get the XPO shipment status for a given reference
	@GetMapping("/getXPOStatus/{ref}/{type}")
	public Object getXPOStatus(@PathVariable String ref, @PathVariable int type) {

		if (authToken.getAccessToken() == null) {
			authToken = getXPOBearerToken();
		}
		int id = 0;
		String refNum = null;
		if (type == 0) {
			id = Integer.parseInt(ref);
			refNum = refNumService.getRefNum(id);
			if (refNum.length() > 20)
				return refNum;
		} else
			refNum = ref;
		String accessToken = "Bearer " + authToken.getAccessToken();

		RestTemplate restTemplate = new RestTemplate();
		String url1 = "https://api.ltl.xpo.com/tracking/1.0/shipments/shipment-status-details?referenceNumbers="
				+ refNum;

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<Object> response = restTemplate.exchange(url1, HttpMethod.GET, entity, Object.class);
			if (id != 0)
				trackResponseService.handleTrackingResponse(response.getBody(), id, 1);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			System.out.println(e.getResponseHeaders());
			return e.getResponseBodyAsString();
		}
	}

	// Get the UPS shipment status for a given reference
	@PostMapping("/getUPSStatus/{ref}/{type}")
	public Object getUPSstatus(@PathVariable String ref, @PathVariable int type) {

		int id = 0;
		String refNum = null;
		if (type == 0) {
			id = Integer.parseInt(ref);
			refNum = refNumService.getRefNum(id);
			if (refNum.length() > 20)
				return refNum;
		} else
			refNum = ref;

		trackRequest.setInquiryNumber(refNum);
		UPSTrackRequest.setTrackRequest(trackRequest);
		String url = "https://wwwcie.ups.com/rest/Track";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<UPS_TrackRequest> entity = new HttpEntity<>(UPSTrackRequest, headers);
		try {
			ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
			if (id != 0)
				trackResponseService.handleTrackingResponse(response.getBody(), id, 2);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			return e.getResponseBodyAsString();
		}
	}

	// Get the Current Status details for a given Booking ID
	@GetMapping("/getCurrentStatus/{bookingID}")
	public Object getCurrentStatus(@PathVariable int bookingID) {

		JSONObject json = new JSONObject();
		json = currentStatusService.getBookingCurrentStatus(bookingID);
		if (json == null)
			return "Booking does not exist in DB or does not have a current status for ID " + bookingID;
		Gson gson = new Gson();
		Object obj = gson.fromJson(json.toString(), Object.class);
		return obj;
	}

	@GetMapping("/getSavedTrackResponse")
	public Object getTrackResponseBlob() {
		Object obj = trackResponseHandler.getBanyanResponse(600);
		return obj;
	}

	@GetMapping("/testSampleBanyanResponse")
	public void testSampleBanyanResponse() {

		JSONArray statuses = null;
		Gson gson = new Gson();
		String jstring = gson.toJson(sampleService.getSampleBanyanTrackresponse());
		JSONObject json;
		try {
			json = new JSONObject(jstring);
			statuses = json.getJSONArray("TrackingStatuses");
			for (int i = statuses.length() - 1; i >= 0; i--) {
				JSONObject statusResponse = statuses.getJSONObject(i);
				System.out.println(i + " " + statusResponse.getString("Code"));
				statusHandlerService.handleLoadStatus(statusResponse);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
