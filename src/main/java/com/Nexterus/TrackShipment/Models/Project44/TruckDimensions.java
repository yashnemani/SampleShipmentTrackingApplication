package com.Nexterus.TrackShipment.Models.Project44;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class TruckDimensions {

	private int length;
	private int width;
	private int height;
	private String unitOfMeasure;

	public TruckDimensions() {
		super();
		this.length=1;
		this.width=1;
		this.height=1;
		this.unitOfMeasure="IN";
	}

	public TruckDimensions(Builder build) {
		this.length = build.length;
		this.width = build.width;
		this.height = build.height;
		this.unitOfMeasure = build.unitOfMeasure;
	}

	public static class Builder {

		private int length;
		private int width;
		private int height;
		private String unitOfMeasure;

		public Builder setLength(int length) {
			this.length = length;
			return this;
		}

		public Builder setWidth(int width) {
			this.width = width;
			return this;
		}

		public Builder setHeight(int height) {
			this.height = height;
			return this;
		}

		public Builder setUnitOfMeasure(String unitOfMeasure) {
			this.unitOfMeasure = unitOfMeasure;
			return this;
		}

		public TruckDimensions build() {
			return new TruckDimensions(this);
		}
	}

	@JsonProperty("length")
	public int getLength() {
		return length;
	}

	@JsonProperty("width")
	public int getWidth() {
		return width;
	}

	@JsonProperty("height")
	public int getHeight() {
		return height;
	}

	@JsonProperty("unitOfMeasure")
	public String getUnitOfMeasure() {
		return unitOfMeasure;
	}
}
