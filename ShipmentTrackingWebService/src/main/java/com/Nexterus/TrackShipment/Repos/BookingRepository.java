package com.Nexterus.TrackShipment.Repos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.Nexterus.TrackShipment.Entities.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer>, BookingRepositoryCustom {
	
	@Query(nativeQuery=true, value="select booking_id from tracking_queue where provider_id=:provider order by booking_id")
	List<BigDecimal> getTrackIdsFromQueue(@Param("provider")int providerId);
}
