package com.Nexterus.TrackShipment.Entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "Booking_Status", schema = "TBB")
public class BookingStatus {

	@Id
	@Column(name = "Booking_Status_ID")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "booking_id")
	private Booking booking;

	@Column(name = "Status_Id")
	private String status;

	@Column(name = "Status_Date")
	private java.sql.Timestamp date;

	@Column(name = "Message")
	private String message;

	@Column(name = "Location")
	private String location;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "status", cascade = CascadeType.ALL)
	private BookingCurrentStatus currentStatus;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public java.sql.Timestamp getDate() {
		return date;
	}

	public void setDate(java.sql.Timestamp date) {
		this.date = date;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BookingCurrentStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(BookingCurrentStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

}
