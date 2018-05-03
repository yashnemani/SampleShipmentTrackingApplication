package com.Nexterus.TrackShipment.Controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
import com.Nexterus.TrackShipment.Models.Banyan.BanyanStatus;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusRequest;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusResponse;
import com.Nexterus.TrackShipment.Models.UPS.TrackRequest;
import com.Nexterus.TrackShipment.Models.UPS.UPS_TrackRequest;
import com.Nexterus.TrackShipment.Models.XPO.OAuth2Token;
import com.Nexterus.TrackShipment.Models.XPO.XPOAccess;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Services.GetRefNum;
import com.Nexterus.TrackShipment.Services.SampleBanyanTrackResponse;
import com.Nexterus.TrackShipment.Services.TrackingResponseHandler;

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
	SampleBanyanTrackResponse sampleService;

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
			trackResponseService.handleTrackingResponse(response.getBody(), 0, 0);
			/*
			 * sampleService.handleTrackingResponse(getSampleBanyanTrackresponse(), 0, 0);
			 */
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			return null;
		}
	}

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

	@GetMapping("/getXPOStatus/{id}")
	public Object getXPOStatus(@PathVariable int id) {

		if (authToken.getAccessToken() == null) {
			authToken = getXPOBearerToken();
		}

		String accessToken = "Bearer " + authToken.getAccessToken();

		String refNum = refNumService.getRefNum(id);
		if (refNum.length() > 20)
			return refNum;
		RestTemplate restTemplate = new RestTemplate();
		String url1 = "https://api.ltl.xpo.com/tracking/1.0/shipments/shipment-status-details?referenceNumbers="
				+ refNum;

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<Object> response = restTemplate.exchange(url1, HttpMethod.GET, entity, Object.class);
			trackResponseService.handleTrackingResponse(response.getBody(), id, 1);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			System.out.println(e.getResponseHeaders());
			return e.getResponseBodyAsString();
		}
	}

	@PostMapping("/getUPSStatus/{id}")
	public Object getUPSstatus(@PathVariable int id) {

		String refNum = refNumService.getRefNum(id);
		if (refNum.length() > 20)
			return refNum;

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
			trackResponseService.handleTrackingResponse(response.getBody(), id, 2);
			return response.getBody();
		} catch (HttpClientErrorException e) {
			System.out.println(e.getStatusCode());
			System.out.println(e.getResponseBodyAsString());
			return e.getResponseBodyAsString();
		}
	}
}
