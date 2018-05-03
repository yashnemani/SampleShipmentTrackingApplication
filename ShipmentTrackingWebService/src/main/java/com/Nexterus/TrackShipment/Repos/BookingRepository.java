package com.Nexterus.TrackShipment.Repos;

import org.springframework.data.jpa.repository.JpaRepository;

import com.Nexterus.TrackShipment.Entities.Booking;

public interface BookingRepository extends JpaRepository<Booking,Integer>,BookingRepositoryCustom {

}
