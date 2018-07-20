package com.Nexterus.TrackShipment.Services.General;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Entities.BookingReferences;
import com.Nexterus.TrackShipment.Models.UPS.ReferenceNumber;
import com.Nexterus.TrackShipment.Models.UPS.TrackRequest;
import com.Nexterus.TrackShipment.Models.UPS.UPS_TrackRequest;
import com.Nexterus.TrackShipment.Repos.BookingRepository;

@Service
public class GetRefNum {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger(GetRefNum.class);
	org.slf4j.Logger nxtLogger = LoggerFactory.getLogger("com.nexterus");
	
	@Autowired
	BookingRepository bookRepo;
	@Autowired
	UPS_TrackRequest UPSTrackRequest;
	@Autowired
	UPS_TrackRequest UPSTrackRequest1;
	@Autowired
	TrackRequest trackRequest;
	@Autowired
	TrackRequest trackRequest1;

	public String getRefNum(int bookingId, int provider) {

		int refType = 0;
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
			log.info("Booking does not have reference of required type! ProNumber");

			reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 1).findAny();
			refType = 1;
			if (!reff.isPresent()) {
				log.info("Booking does not have reference of required type! BOLNumber/ProNumber");
				reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 2).findAny();
				refType = 2;
				if (!reff.isPresent()) {
					log.info("Booking does not have reference of required type! PO_Number/ProNumber");
					return "Booking does not have reference of required type! BOLNumber/ProNumber";
				}
			}
		}

		BookingReferences ref = reff.get();
		String refNum = ref.getReference();
		return refNum;
	}

	public UPS_TrackRequest get_UPS_TrackRefs(int bookingId, int provider) {
		int refType = 0;
		if (!bookRepo.existsById(bookingId))
			return null;

		Optional<Booking> bookT = null;
		bookT = bookRepo.findById(bookingId);
		final Booking book = bookT.get();
		Supplier<Stream<BookingReferences>> refStreamSupplier = () -> book.getReferences().stream();

		if (book.getReferences() == null)
			return null;

		Optional<BookingReferences> reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 0).findAny();
		if (!reff.isPresent()) {
			log.info("Booking does not have reference of required type! ProNumber");

			reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 1).findAny();
			refType = 1;
			if (!reff.isPresent()) {
				log.info("Booking does not have reference of required type! PO_Number/ProNumber");
				reff = refStreamSupplier.get().filter(a -> a.getRef_type() == 2).findAny();
				refType = 2;
				if (!reff.isPresent()) {
					log.info("Booking does not have reference of required type! BOLNumber/PO_Number/ProNumber");
					return null;
				}
			}
		}

		BookingReferences ref = reff.get();
		String refNum = ref.getReference();
		if (refType == 0) {
			trackRequest.setInquiryNumber(refNum);
			trackRequest.setPickupDateRange(null);
			trackRequest.setRefNum(null);
			trackRequest.setShipType(null);
			UPSTrackRequest.setTrackRequest(trackRequest);
			return UPSTrackRequest;
		} else {
			ReferenceNumber refnum = new ReferenceNumber();
			refnum.setValue(refNum);
			trackRequest1.setRefNum(refnum);
			trackRequest1.setInquiryNumber(null);
			UPSTrackRequest1.setTrackRequest(trackRequest1);
			return UPSTrackRequest1;
		}
	}
}
