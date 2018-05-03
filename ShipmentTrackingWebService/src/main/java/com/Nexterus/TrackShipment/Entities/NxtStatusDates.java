package com.Nexterus.TrackShipment.Entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Booking_Nxt_Status_Dates", schema = "TBB")
public class NxtStatusDates {

	@Id
	@Column(name = "id")
	@GeneratedValue
	private int id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bookingId")
	private Booking booking;

	@Column(name = "dt_entered")
	private java.sql.Timestamp dt_entered;

	@Column(name = "dt_pkup")
	private java.sql.Timestamp dt_pickedup;

	@Column(name = "dt_dlvr")
	private java.sql.Timestamp dt_delivered;

	public NxtStatusDates() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public java.sql.Timestamp getDt_entered() {
		return dt_entered;
	}

	public void setDt_entered(java.sql.Timestamp dt_entered) {
		this.dt_entered = dt_entered;
	}

	public java.sql.Timestamp getDt_pickedup() {
		return dt_pickedup;
	}

	public void setDt_pickedup(java.sql.Timestamp dt_pickedup) {
		this.dt_pickedup = dt_pickedup;
	}

	public java.sql.Timestamp getDt_delivered() {
		return dt_delivered;
	}

	public void setDt_delivered(java.sql.Timestamp dt_delivered) {
		this.dt_delivered = dt_delivered;
	}

}
