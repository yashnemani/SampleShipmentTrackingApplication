package com.Nexterus.TrackShipment.Services.General;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.pmw.tinylog.Logger;
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

	Integer XPO_Batch = 16;
	Integer UPS_Batch = 11;
	Integer xpoDlvrCount = 2;
	Integer upsDlvrCount = 2;

	@Scheduled(cron = "0 21 * * * ?")
	public void banyanProductionTrackingScheduler() {
		trackController.getBanyanStatuses();
	}

	@Scheduled(cron = "0 0/4 * * * ?")
	public void XPO_TrackingScheduler() {

		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(1);
		int batchSize = trackIds.size() / 10;
		if (trackIds.size() % 10 == 0)
			batchSize = batchSize - 1;
		System.out.println("Track Size: " + trackIds.size() + " BatchSize: " + batchSize);
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
		System.out.println("Batch: " + XPO_Batch);
		if (min != 0) {
			min = min - xpoDlvrCount;
			System.out.println("Shipments delivered in previous Batch " + xpoDlvrCount);
			System.out.println("Adjusting Batch Size accordingly...... New min is " + min);
		}
		xpoDlvrCount = 0;
		Logger.info("Processing a new XPO Batch from TrackingQueue... Batch:" + XPO_Batch);
		for (int i = min; i < max; i++) {
			System.out.println("Tracking ID: " + trackIds.get(i));
			trackController.getXPOStatus(trackIds.get(i).toString());
		}
		if (XPO_Batch < batchSize)
			XPO_Batch++;
		else
			XPO_Batch = 0;
	}

	@Scheduled(cron = "0 0/6 * * * ?")
	public void UPS_TrackingScheduler() {

		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(2);
		int batchSize = trackIds.size() / 10;
		if (trackIds.size() % 10 == 0)
			batchSize = batchSize - 1;
		System.out.println("Track Size: " + trackIds.size() + " BatchSize: " + batchSize);
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
		System.out.println("Batch: " + UPS_Batch);

		if (min != 0) {
			min = min - upsDlvrCount;
			System.out.println("Shipments delivered in previous Batch " + upsDlvrCount);
			System.out.println("Adjusting Batch Size accordingly...... New min is " + min);
		}
		upsDlvrCount = 0;
		Logger.info("Processing a new UPS Batch from TrackingQueue... Batch:" + UPS_Batch);
		for (int i = min; i < max; i++) {
			System.out.println("Tracking ID: " + trackIds.get(i));
			trackController.getUPSstatus(trackIds.get(i).toString());
		}
		if (UPS_Batch < batchSize)
			UPS_Batch++;
		else
			UPS_Batch = 0;
	}
	
	@Scheduled(cron = "0 2 * * * ?")
	public void project44_TrackingScheduler() {
		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(3);
		for (int i = 0; i < trackIds.size(); i++) {
			System.out.println("Tracking ID: " + trackIds.get(i));
			trackController.getTruckLoadStatus(trackIds.get(i).intValue());
		}
	}

	public void trackDeliveredCount(int provider) {
		if (provider == 1)
			xpoDlvrCount++;
		if (provider == 2)
			upsDlvrCount++;
	}

	@Scheduled(cron = "0 46 * * * ?")
	public void updateProNumbers() {
		bookRepo.updateProNumbers();
	}
}
