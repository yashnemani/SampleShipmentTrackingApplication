/*package com.Nexterus.TrackShipment.RepoImpls;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.Nexterus.TrackShipment.Repos.BookingStatusRepositoryCustom;

@Repository
public class BookingStatusRepositoryImpl  implements BookingStatusRepositoryCustom{

	@Autowired
	DataSource ds;
	@Autowired
	JdbcTemplate jdbc;

	public void setJdbcTemplate(DataSource ds) {

		this.jdbc = new JdbcTemplate(ds);
	}
	@Override
	public String findEdiStatus(String status, int provider) {
		
		setJdbcTemplate(ds);
		String sql =" select ";
		return null;
	}

}*/
