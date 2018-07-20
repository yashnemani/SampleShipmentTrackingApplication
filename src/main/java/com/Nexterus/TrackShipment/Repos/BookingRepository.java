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
	
	@Query(nativeQuery=true, value="select carrier_code from booking where booking_id=:bookingId")
	String getSCAC(@Param ("bookingId") Integer bookingId);
	
	@Query(nativeQuery=true, value="select update_old_status from provider_carrier where carrier_code=:scac")
	String getUpdateStatusFlag(@Param ("scac") String scac);
	
	@Query(nativeQuery=true,value="select reference from booking_reference where reference_type=3 and booking_id=:id")
	String findLoadIdReference(@Param("id") int bookingId);
}
