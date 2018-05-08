package com.Nexterus.TrackShipment.Repos;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.Nexterus.TrackShipment.Entities.BookingStatus;

public interface BookingStatusRepository extends CrudRepository<BookingStatus, Integer> {

	@Query("select status from BookingStatus where bookingId=:id")
	List<String> findStatusByBookingID(@Param("id") int bookingId);

	@Query(nativeQuery = true, value = "select EDI_CODE from EDI_STATUS_MAPPING where PROVIDER_ID=:proID and STATUS_CODE=:code")
	String findEdiStatus(@Param("proID") int provider, @Param("code") String code);

	@Query(nativeQuery = true, value = "select NXT_STATUS_CODE from EDI_STATUS_CODES where EDI_STATUS_CODE=:code")
	String findNxtStatus(@Param("code") String code);
	
	@Query(nativeQuery = true, value = "select booking_ID from Booking_Reference where Reference_Type=:type and reference=:ref")
	List<BigDecimal> findBookingByReference(@Param("type")int type, @Param("ref")String ref);
}
