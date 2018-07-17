package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Location44 {
	@Autowired
	private Address44 address;
	@Autowired
	private Contact44 contact;

	public Location44() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Location44(Address44 address, Contact44 contact, String id) {
		super();
		this.address = address;
		this.contact = contact;
	}

	@JsonProperty("address")
	public Address44 getAddress() {
		return address;
	}
	@JsonProperty("contact")
	public Contact44 getContact() {
		return contact;
	}
	}
