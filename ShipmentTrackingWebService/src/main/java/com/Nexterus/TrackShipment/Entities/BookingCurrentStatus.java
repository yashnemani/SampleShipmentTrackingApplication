package com.Nexterus.TrackShipment.Entities;

import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Booking_CurrentStatus", schema = "TBB")
public class BookingCurrentStatus {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "bookingid")
	private Booking booking;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "BOOK_STATUS_ID")
	private BookingStatus status;

	@Column(name = "Shipment_Status")
	private String shipStatus;

	@Column(name = "Message")
	private String message;

	@Column(name = "Shipment_State")
	private String shipState;

	@Column(name = "Status_Date")
	private java.sql.Timestamp date;

	@Column(name = "Location")
	private String location;

	@Column(name = "EST_DLVR_DT")
	private Timestamp estDeliveryDt;

	@Column(name = "EST_PKUP_DT")
	private Timestamp estPickupDt;

	@Column(name = "LAST_UPDATED")
	private Timestamp lastUpdatedDt;

	public BookingCurrentStatus() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShipStatus() {
		return shipStatus;
	}

	public void setShipStatus(String shipStatus) {
		this.shipStatus = shipStatus;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getShipState() {
		return shipState;
	}

	public void setShipState(String shipState) {
		this.shipState = shipState;
	}

	public java.sql.Timestamp getDate() {
		return date;
	}

	public void setDate(java.sql.Timestamp date) {
		this.date = date;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public Timestamp getEstDeliveryDt() {
		return estDeliveryDt;
	}

	public void setEstDeliveryDt(Timestamp estDeliveryDt) {
		this.estDeliveryDt = estDeliveryDt;
	}

	public Timestamp getEstPickupDt() {
		return estPickupDt;
	}

	public void setEstPickupDt(Timestamp estPickupDt) {
		this.estPickupDt = estPickupDt;
	}

	public Timestamp getLastUpdatedDt() {
		return lastUpdatedDt;
	}

	public void setLastUpdatedDt() {
		this.lastUpdatedDt = new Timestamp(System.currentTimeMillis());
	}
}
