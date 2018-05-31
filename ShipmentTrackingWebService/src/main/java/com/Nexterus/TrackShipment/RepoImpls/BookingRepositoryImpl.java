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

	@Override
	public void updateProNumbers() {

		setJdbcTemplate(ds);
		String sql = "insert into booking_reference(booking_id,REFERENCE_TYPE,reference)"
				+ "select rt_qte_id,0,pro_no from rate_quote_address where "
				+ "rt_qte_id in (select booking_id from booking where  booking_id not in (select booking_id from booking_reference where REFERENCE_TYPE=0) and provider_id!=0) and pro_no is not null";
		int  r = jdbc.update(sql);
		System.out.println(r+" Pro Number/s updated!");
	}
}
