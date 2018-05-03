package com.Nexterus.TrackShipment.Repos;

import org.springframework.data.repository.CrudRepository;

import com.Nexterus.TrackShipment.Entities.BookingReferences;

public interface BookingReferencesRepository extends CrudRepository<BookingReferences, Integer>, BookingReferencesRepositoryCustom {
}
