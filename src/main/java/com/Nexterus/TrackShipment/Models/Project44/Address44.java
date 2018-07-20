package com.Nexterus.TrackShipment.Models.Project44;

import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class Address44 {

	private String postalCode;
	private List<String> addressLines;
	private String city;
	private String state;
	private String country;

	public Address44() {
		super();
	}

	public Address44(Builder build) {
		this.postalCode = build.postalCode;
		this.addressLines = build.addressLines;
		this.city = build.city;
		this.state = build.state;
		this.country = build.country;
	}

	public static class Builder {
		private String postalCode;
		private List<String> addressLines;
		private String city;
		private String state;
		private String country;

		public Builder setPostalCode(String postalCode) {
			this.postalCode = postalCode;
			return this;
		}

		public Builder setAddressLines(List<String> addressLines) {
			this.addressLines = addressLines;
			return this;
		}

		public Builder setCity(String city) {
			this.city = city;
			return this;
		}

		public Builder setState(String state) {
			this.state = state;
			return this;
		}

		public Builder setCountry(String country) {
			this.country = country;
			return this;
		}

		public Address44 build() {
			return new Address44(this);
		}
	}

	@JsonProperty("postalCode")
	public String getPostalCode() {
		return postalCode;
	}

	@JsonProperty("addressLines")
	public List<String> getAddressLines() {
		return addressLines;
	}

	@JsonProperty("city")
	public String getCity() {
		return city;
	}

	@JsonProperty("state")
	public String getState() {
		return state;
	}

	@JsonProperty("country")
	public String getCountry() {
		return country;
	}
}
