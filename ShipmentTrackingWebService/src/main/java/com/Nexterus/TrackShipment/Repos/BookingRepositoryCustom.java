package com.Nexterus.TrackShipment.Repos;

import com.Nexterus.TrackShipment.Entities.Booking;

public interface BookingRepositoryCustom {
	void deleteFromTrackingQueue(int bookingId);
	void updateProNumbers();
	void refresh(Booking book);
}
