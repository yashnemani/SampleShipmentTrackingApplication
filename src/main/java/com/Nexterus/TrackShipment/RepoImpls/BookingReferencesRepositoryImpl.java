package com.Nexterus.TrackShipment.RepoImpls;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.Nexterus.TrackShipment.Repos.BookingReferencesRepositoryCustom;

@Repository
public class BookingReferencesRepositoryImpl implements BookingReferencesRepositoryCustom {

	@Autowired
	DataSource ds;
	@Autowired
	JdbcTemplate jdbc;

	static Map<String, String> refMap = new HashMap<String, String>();

	public void setJdbcTemplate(DataSource ds) {

		this.jdbc = new JdbcTemplate(ds);
	}

	@Override
	public Map<String, String> getReferencesByID(int id) {

		setJdbcTemplate(ds);
		String sql = " select reference_type,reference from booking_reference where booking_id=?";

		PreparedStatementSetter prep = new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement prepStatement) throws SQLException {
				prepStatement.setInt(1, id);
			}
		};

		List<Integer> referenceMapList = new ArrayList<>();

		referenceMapList = jdbc.query(sql, prep, new ReferenceMapper());

		return refMap;
	}

	private static class ReferenceMapper implements RowMapper<Integer> {

		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {

			Integer in = 0;
			refMap.put(rs.getString(1), rs.getString(2));

			return in;
		}

	}

}
