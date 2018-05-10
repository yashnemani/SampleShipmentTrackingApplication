package com.Nexterus.TrackShipment.RepoImpls;

import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.Nexterus.TrackShipment.Entities.Booking;
import com.Nexterus.TrackShipment.Repos.BookingRepositoryCustom;

@Service
public class BookingRepositoryImpl implements BookingRepositoryCustom {

	@PersistenceContext
	private EntityManager em;
	@Autowired
	DataSource ds;
	@Autowired
	JdbcTemplate jdbc;

	public void setJdbcTemplate(DataSource ds) {
		this.jdbc = new JdbcTemplate(ds);
	}

	@Transactional
	public void refresh(Booking book) {
		em.refresh(em.merge(book));
	}

	@Override
	public void deleteFromTrackingQueue(int bookingId) {

		setJdbcTemplate(ds);
		String sql = "delete from tracking_queue where booking_id=?";
		PreparedStatementSetter prep = new PreparedStatementSetter() {
			@Override
			public void setValues(java.sql.PreparedStatement prepstatement) throws SQLException {
				prepstatement.setInt(1, bookingId);
			}
		};
		jdbc.update(sql, prep);
	}
}
