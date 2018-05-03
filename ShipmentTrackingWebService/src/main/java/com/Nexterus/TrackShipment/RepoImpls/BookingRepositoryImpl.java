package com.Nexterus.TrackShipment.RepoImpls;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Repos.BookingRepositoryCustom;

@Service
public class BookingRepositoryImpl implements BookingRepositoryCustom{
	
	   @PersistenceContext
	    private EntityManager em;
	

	@Transactional
	public void refresh(Booking book) {
		em.refresh(em.merge(book));
		/*em.getTransaction().commit();*/
		/*em.clear();*/
	}

}
