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

	/*@Scheduled(cron = "0 1 * * * ?") */
	public void banyanTrackingScheduler() {
		trackController.getBanyanStatuses();
	}

	@Scheduled(cron = "0 0/5 * * * ?") 
	public void XPO_TrackingScheduler() {

		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(1);
		trackIds.forEach(a -> trackController.getUPSstatus(a.toString(), 0));
	}

	/*@Scheduled(cron = "0 0/5 * * * ?")*/
	public void UPS_TrackingScheduler() {

		List<BigDecimal> trackIds = new ArrayList<>();
		trackIds = bookRepo.getTrackIdsFromQueue(2);
		trackIds.forEach(a -> trackController.getUPSstatus(a.toString(), 0));
	}
}
