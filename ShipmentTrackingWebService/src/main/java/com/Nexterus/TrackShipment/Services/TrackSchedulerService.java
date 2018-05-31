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

	Integer XPO_Batch = 0;
	Integer UPS_Batch = 0;
	Integer Update_Batch = 20;

	Integer xpoDlvrCount = 0;
	Integer upsDlvrCount = 0;

	@Scheduled(cron = "0 40 * * * ?")
	public void banyanTrackingScheduler() {
		trackController.getBanyanStatuses();
	}

	 @Scheduled(cron = "0 0/6 * * * ?") 
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
		for (int i = min; i < max; i++) {
			System.out.println("Tracking ID: " + trackIds.get(i));
			trackController.getXPOStatus(trackIds.get(i).toString(), 0, true);
		}
		if (XPO_Batch < batchSize)
			XPO_Batch++;
		else
			XPO_Batch = 0;
	}

	 @Scheduled(cron = "0 0/8 * * * ?") 
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

		for (int i = min; i < max; i++) {
			System.out.println("Tracking ID: " + trackIds.get(i));
			trackController.getUPSstatus(trackIds.get(i).toString(), 0, true);
		}
		if (UPS_Batch < batchSize)
			UPS_Batch++;
		else
			UPS_Batch = 0;
	}

	public void trackDeliveredCount(int provider) {

		if (provider == 1)
			xpoDlvrCount++;
		if (provider == 2)
			upsDlvrCount++;
	}

	@Scheduled(cron = "0 32 * * * ?")
	public void updateProNumbers() {
		bookRepo.updateProNumbers();
	}

/*	@Scheduled(cron = "0 0/10 * * * ?")
	public void ups_updateActivity() {
		List<BigDecimal> deliveredIds = new ArrayList<>();
		deliveredIds = bookRepo.findDeliveredBookingsByProvider(2);
		int batchSize = deliveredIds.size() / 10;
		if (deliveredIds.size() % 10 == 0)
			batchSize = batchSize - 1;
		System.out.println("Track Size: " + deliveredIds.size() + " BatchSize: " + batchSize);
		int min, max = 0;
		if (Update_Batch < batchSize) {
			min = Update_Batch * 10;
			max = min + 10;
		} else {
			min = Update_Batch * 10;
			max = deliveredIds.size();
		}
		System.out.println();
		System.out.println("Update Activity ........");
		System.out.println("Batch: " + Update_Batch);

		for (int i = min; i < max; i++) {
			System.out.println("ID: " + deliveredIds.get(i));
			trackController.getUPSstatus(deliveredIds.get(i).toString(), 0, true);
		}
		if (Update_Batch < batchSize)
			Update_Batch++;
		else
			Update_Batch = 0;
	}*/
}
