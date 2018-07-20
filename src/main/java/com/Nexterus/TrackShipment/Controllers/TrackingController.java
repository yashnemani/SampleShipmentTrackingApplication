package com.Nexterus.TrackShipment.Controllers;

import java.util.Arrays;

import org.slf4j.LoggerFactory;
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
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.Nexterus.TrackShipment.Models.TrackingStatusJson;
import com.Nexterus.TrackShipment.Models.Banyan.AuthenticationData;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusRequest;
import com.Nexterus.TrackShipment.Models.Banyan.TrackingStatusResponse;
import com.Nexterus.TrackShipment.Models.Project44.Auth44;
import com.Nexterus.TrackShipment.Models.Project44.TrackLoadStatusResponse;
import com.Nexterus.TrackShipment.Models.UPS.TrackRequest;
import com.Nexterus.TrackShipment.Models.UPS.UPS_TrackRequest;
import com.Nexterus.TrackShipment.Models.XPO.OAuth2Token;
import com.Nexterus.TrackShipment.Models.XPO.XPOAccess;
import com.Nexterus.TrackShipment.Repos.BookingRepository;
import com.Nexterus.TrackShipment.Services.Banyan.BanyanStatusHandlerService;
import com.Nexterus.TrackShipment.Services.Banyan.BanyanTrackResponseHandler;
import com.Nexterus.TrackShipment.Services.Banyan.BuildTrackingStatusJson;
import com.Nexterus.TrackShipment.Services.General.GetCurrentStatus;
import com.Nexterus.TrackShipment.Services.General.GetRefNum;
import com.Nexterus.TrackShipment.Services.General.TrackingResponseHandler;
import com.Nexterus.TrackShipment.Services.Project44.HandleTruckLoadStatus;
import com.Nexterus.TrackShipment.Services.UPS.UPS_UpdateActivity;
import com.Nexterus.TrackShipment.Services.XPO.XPO_UpdateEvents;

@RestController
@RequestMapping("/")
public class TrackingController {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(TrackingController.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	@Autowired
	OAuth2Token authToken;
	@Autowired
	UPS_TrackRequest UPSTrackRequest;
	@Autowired
	UPS_TrackRequest UPSTrackRequest1;
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
	BanyanTrackResponseHandler banyanTrackResponseHandler;
	@Autowired
	BanyanStatusHandlerService statusHandlerService;
	@Autowired
	UPS_UpdateActivity updateActivitySerrvice;
	@Autowired
	XPO_UpdateEvents xpoEventService;

	// Get a list of all updated Banyan shipment statuses
	@GetMapping("/getBanyanStatuses")
	public TrackingStatusResponse getBanyanStatuses() {

		AuthenticationData authData = new AuthenticationData();
		TrackingStatusRequest statusRequest = new TrackingStatusRequest();
		statusRequest.setAuthData(authData);

		RestTemplate restTemplate = new RestTemplate();
		String url = "https://ws.logistics.banyantechnology.com/services/api/rest/GetTrackingStatuses";

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<TrackingStatusRequest> entity = new HttpEntity<>(statusRequest, headers);
		try {
			ResponseEntity<TrackingStatusResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity,
					TrackingStatusResponse.class);
			banyanTrackResponseHandler.handleTrackResponse(response.getBody());
			return response.getBody();
		} catch (HttpClientErrorException e) {
			nxtLogger.error("Banyan Tracking Request failed! Error " + e.getMessage());
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
	@GetMapping("/getXPOStatus/{ref}")
	public Object getXPOStatus(@PathVariable String ref) {

		if (authToken.getAccessToken() == null) {
			authToken = getXPOBearerToken();
		}
		int id = 0;
		String refNum = null;
			id = Integer.parseInt(ref);
			refNum = refNumService.getRefNum(id, 1);
			if (refNum.length() > 20)
				return refNum;
	
		String accessToken = "Bearer " + authToken.getAccessToken();
		RestTemplate restTemplate = new RestTemplate();
		String url1 = "https://api.ltl.xpo.com/tracking/1.0/shipments/" + refNum + "/tracking-events";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", accessToken);

		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
		try {
			ResponseEntity<Object> response = restTemplate.exchange(url1, HttpMethod.GET, entity, Object.class);
		
				xpoEventService.updateEvents(response.getBody(), id, 1, refNum);
				return response.getBody();
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (e.getStatusCode().value() == 401) {
				authToken = getXPOBearerToken();
				getXPOStatus(ref);
			}
			log.info(e.getResponseBodyAsString());
			if (e.getMessage() != null)
				nxtLogger.error("XPO Tracking Request failed for " + ref + " Error " + e.getMessage());
			return e.getResponseBodyAsString();
		}
	}

	// Get the UPS shipment status for a given reference
	@PostMapping("/getUPSStatus/{ref}")
	public Object getUPSstatus(@PathVariable String ref) {

		int id = 0;
		String refNum = null;
			id = Integer.parseInt(ref);
			UPSTrackRequest1 = refNumService.get_UPS_TrackRefs(id, 2);
			if (UPSTrackRequest1 == null)
				return null;
			refNum = UPSTrackRequest1.getTrackRequest().getInquiryNumber();

		/* String url = "https://wwwcie.ups.com/rest/Track"; */
		String prodUrl = "https://onlinetools.ups.com/rest/Track";

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<UPS_TrackRequest> entity = new HttpEntity<>(UPSTrackRequest1, headers);
		try {
			ResponseEntity<Object> response = restTemplate.exchange(prodUrl, HttpMethod.POST, entity, Object.class);
				updateActivitySerrvice.updateActivity(response.getBody(), id, 2, refNum);
				return response.getBody();
		} catch (HttpClientErrorException e) {
			log.info("UPS Tracking Request failed for " + ref + " Error " + e.getMessage());
			return e.getResponseBodyAsString();
		}
	}

	@Autowired
	HandleTruckLoadStatus handleTLStatus;

	// Project44 Get TruckLoad Statuses
	@GetMapping("/getTruckLoadStatus/{id}")
	public Object getTruckLoadStatus(@PathVariable Integer id) {
		log.info("Project44 ID being Tracked: "+id);
		String ref = bookRepo.findLoadIdReference(id);
		int refId = Integer.parseInt(ref);
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		Auth44 auth = new Auth44();
		headers.add("Authorization", auth.getBasic());
		HttpEntity<HttpHeaders> entity = new HttpEntity<>(headers);
		log.info(headers.get("Authorization").toString());
		String url = "https://test.p-44.com/api/v3/tl/shipments/" + refId + "/statuses";
		try {
			ResponseEntity<TrackLoadStatusResponse> response = restTemplate.exchange(url, HttpMethod.GET, entity,
					TrackLoadStatusResponse.class);
			handleTLStatus.handle_TL_Status(response.getBody());
			return response.getBody();
		} catch (HttpClientErrorException e) {
			nxtLogger.error("Project44 Get TruckLoad Status Failed! " + refId + " Error: " + e.getMessage());
			return e.getResponseBodyAsString();
		}
	}

	// Test Services
	@GetMapping("/getSavedTrackResponse/{id}")
	public TrackingStatusResponse getTrackResponseBlob(@PathVariable int id) {

		TrackingStatusResponse trackResponse = new TrackingStatusResponse();
		trackResponse = banyanTrackResponseHandler.getBanyanResponse(id);
		return trackResponse;
	}

	@GetMapping("/testSampleBanyanResponse/{id}")
	public void testSampleBanyanResponse(@PathVariable int id) {
		if (id != 0)
			banyanTrackResponseHandler.handleTrackResponse(banyanTrackResponseHandler.getBanyanResponse(id));
	}

	@Autowired
	BuildTrackingStatusJson buildService;
	@Autowired
	TrackingStatusJson trackingStatusJson;

	@PostMapping("/updateOldStatus")
	public TrackingStatusJson updateOldStatus(TrackingStatusJson trackingStatusJson) {

		String testUri = "http://nfr-lin-nexus1-dev/tracking/status";
		/* String prodUri = "http://nfr-lin-nexus1-prod/tracking/status"; */
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_OCTET_STREAM));
		HttpEntity<TrackingStatusJson> entity = new HttpEntity<>(trackingStatusJson, headers);
		try {
			ResponseEntity<Object> response = restTemplate.exchange(testUri, HttpMethod.POST, entity, Object.class);
			log.info("Response " + response.getStatusCode());
		} catch (Exception e) {
			nxtLogger.error("Exception " + e.getMessage() + " " + e.getStackTrace());
		}
		return trackingStatusJson;
	}
}
