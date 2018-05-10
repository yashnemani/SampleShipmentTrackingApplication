package com.Nexterus.TrackShipment.Entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "Booking_Reference", schema = "TBB")
public class BookingReferences {

	@Id
	@Column(name = "Booking_Reference_Id")
	@GeneratedValue(strategy= GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "booking_id")
	private Booking booking;

	public Booking getBooking() {
		return booking;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	@Column(name = "Reference_Type")
	private Integer ref_type;

	@Column(name = "Reference")
	private String reference;

	public BookingReferences() {
		super();
	}

	public Integer getId1() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getRef_type() {
		return ref_type;
	}

	public void setRef_type(Integer ref_type) {
		this.ref_type = ref_type;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

}
