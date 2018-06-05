package com.Nexterus.TrackShipment.Entities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.sql.rowset.serial.SerialException;

import org.springframework.data.domain.Persistable;
import org.springframework.stereotype.Component;

@Entity
@Component
@Table(name = "Booking", schema = "TBB")
public class Booking implements Persistable<Integer> {

	@Id
	@Column(name = "Booking_Id")
	private Integer booking_id;

	@Column(name = "Org_Id")
	private Integer org_id;

	@Column(name = "CARRIER_CODE")
	private String CARRIER_CODE;

	@Column(name = "Document")
	@Lob
	private byte[] fullLoad;

	@Column(name = "PROVIDER_ID")
	private Integer PROVIDER_ID;

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "booking", cascade = CascadeType.MERGE)
	private Set<BookingReferences> references;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "booking", cascade = CascadeType.ALL)
	private List<BookingStatus> statuses;

	@OneToOne(fetch = FetchType.EAGER, mappedBy = "booking", cascade = CascadeType.MERGE)
	private BookingCurrentStatus currentStatus;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "booking", cascade = CascadeType.MERGE)
	private NxtStatusDates statusDates;

	@Transient
	private boolean update;

	public Booking() {
	}

	public Integer getBooking_id() {
		return booking_id;
	}

	public void setBooking_id(Integer booking_id) {
		this.booking_id = booking_id;
	}

	public Integer getOrg_id() {
		return org_id;
	}

	public void setOrg_id(Integer org_id) {
		this.org_id = org_id;
	}

	public String getCARRIER_CODE() {
		return CARRIER_CODE;
	}

	public void setCARRIER_CODE(String CARRIER_CODE) {
		this.CARRIER_CODE = CARRIER_CODE;
	}

	public byte[] getFullLoad() {
		return fullLoad;
	}

	public void setFullLoad(Object fullLoad) throws SerialException, SQLException, IOException {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(fullLoad);
			byte[] yourBytes = bos.toByteArray();
			this.fullLoad = yourBytes;
		} finally {
			out.close();
			bos.close();
		}
	}

	public Integer getPROVIDER_ID() {
		return PROVIDER_ID;
	}

	public void setPROVIDER_ID(Integer pROVIDER_ID) {
		PROVIDER_ID = pROVIDER_ID;
	}

	@Override
	public Integer getId() {
		return this.booking_id;
	}

	@Override
	public boolean isNew() {
		return !this.update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public Set<BookingReferences> getReferences() {
		return references;
	}

	public void setReferences(Set<BookingReferences> references) {
		this.references = references;
	}

	public List<BookingStatus> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<BookingStatus> statuses) {
		this.statuses = statuses;
	}

	public BookingCurrentStatus getCurrentStatus() {
		return currentStatus;
	}

	public void setCurrentStatus(BookingCurrentStatus currentStatus) {
		this.currentStatus = currentStatus;
	}

	public NxtStatusDates getStatusDates() {
		return statusDates;
	}

	public void setStatusDates(NxtStatusDates statusDates) {
		this.statusDates = statusDates;
	}

}
