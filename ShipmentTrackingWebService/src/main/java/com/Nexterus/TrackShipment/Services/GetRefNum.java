package com.Nexterus.TrackShipment.Services;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingReferences;
import com.Nexterus.TrackShipment.Repos.BookingRepository;

@Service
public class GetRefNum {

	@Autowired
	BookingRepository bookRepo;

	public String getRefNum(int bookingId, int provider) {

		if (!bookRepo.existsById(bookingId))
			return ("Booking does not exist in DB for ID " + bookingId);

		Optional<Booking> bookT = null;
		bookT = bookRepo.findById(bookingId);
		final Booking book = bookT.get();
		Supplier<Stream<BookingReferences>> refStreamSupplier = () -> book.getReferences().stream();

		if (book.getReferences() == null)
			return ("Booking does not have references of required type!");

		Optional<BookingReferences> reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 0).findAny();
		if (!reff.isPresent()) {
			System.out.println("Booking does not have reference of required type! ProNumber");

			if (provider == 1) {
				reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 1).findAny();
				if (!reff.isPresent()) {
					System.out.println("Booking does not have reference of required type! BOLNumber/ProNumber");
					reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 2).findAny();
					if (!reff.isPresent()) {
						System.out.println("Booking does not have reference of required type! PO_Number/ProNumber");
						return "Booking does not have reference of required type! BOLNumber/ProNumber";
					}
				}
			}

			if (provider == 2) {
				reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 2).findAny();
				if (!reff.isPresent()) {
					System.out.println("Booking does not have reference of required type! PO_Number/ProNumber");
					reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 1).findAny();
					if (!reff.isPresent()) {
						System.out.println(
								"Booking does not have reference of required type! BOLNumber/PO_Number/ProNumber");
						return "Booking does not have reference of required type! BOLNumber/ProNumber";
					}
				}
			}
		}

		BookingReferences ref = reff.get();
		String refNum = ref.getReference();
		return refNum;
	}
}
