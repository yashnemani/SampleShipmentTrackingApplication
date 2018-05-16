package com.Nexterus.TrackShipment.Services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Controllers.TrackingController;
import com.Nexterus.TrackShipment.Repos.BookingRepository;

@Service
public class TrackSchedulerService {

	@Autowired
	TrackingController trackController;
	@Autowired
	BookingRepository bookRepo;

	Integer XPO_Batch = 4;
	Integer UPS_Batch = 1;

	@Scheduled(cron = "0 1 * * * ?")
	public void banyanTrackingScheduler() {
		trackController.getBanyanStatuses();
	}

	@Scheduled(cron = "0 0/20 * * * ?")
	public void XPO_TrackingScheduler() {

		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(1);
		int batchSize = trackIds.size() / 10;
		System.out.println("Track Size: "+trackIds.size()+" BatchSize: "+batchSize);
		int min, max = 0;
		if (XPO_Batch < batchSize) {
			min = XPO_Batch * 10;
			max = min + 10;
		} else {
			min = XPO_Batch * 10;
			max = trackIds.size();
		}
		
		System.out.println();
		System.out.println("XPO Tracking ........");
		System.out.println("Batch: "+XPO_Batch);
		
		for (int i = min; i < max; i++) {
			System.out.println("Tracking ID: "+trackIds.get(i));
			trackController.getXPOStatus(trackIds.get(i).toString(), 0);
		}
		if (XPO_Batch < batchSize)
			XPO_Batch++;
		else
			XPO_Batch = 0;
	}

	@Scheduled(cron = "0 0/30 * * * ?")
	public void UPS_TrackingScheduler() {

		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(2);
		int batchSize = trackIds.size() / 10;
		System.out.println("Track Size: "+trackIds.size()+" BatchSize: "+batchSize);
		int min, max = 0;
		if (UPS_Batch < batchSize) {
			min = UPS_Batch * 10;
			max = min + 10;
		} else {
			min = UPS_Batch * 10;
			max = trackIds.size();
		}
		System.out.println();
		System.out.println("UPS Tracking ........");
		System.out.println("Batch: "+UPS_Batch);
		
		for (int i = min; i < max; i++) {
			System.out.println("Tracking ID: "+trackIds.get(i));
			trackController.getUPSstatus(trackIds.get(i).toString(), 0);
		}
		if (UPS_Batch < batchSize)
			UPS_Batch++;
		else
			UPS_Batch = 0;
	}
}
