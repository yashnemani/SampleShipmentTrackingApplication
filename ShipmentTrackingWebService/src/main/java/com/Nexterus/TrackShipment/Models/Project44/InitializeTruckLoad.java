package com.Nexterus.TrackShipment.Models.Project44;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class InitializeTruckLoad {

	private Integer id = null;
	@Autowired
	private CarrierIdentifier carrierIdentifier;
	@Autowired
	private List<ShipmentIdentifier> shipmentIdentifiers;
	@Autowired
	private List<ShipmentStops> shipmentStops;
	@Autowired
	private CapacityProviderAccountGroup capacityProviderAccountGroup;
	@Autowired
	private List<EquipmentIdentifiers> equipmentIdentifiers;
	@Autowired
	private ShippingDetails shippingDetails;
	@Autowired
	private ApiConfiguration apiConfiguration;
	@Autowired
	private List<Attribute> attributes;

	public InitializeTruckLoad() {
		super();
	}

	public InitializeTruckLoad(Builder build) {
		this.carrierIdentifier = build.carrierIdentifier;
		this.shipmentIdentifiers = build.shipmentIdentifiers;
		this.shipmentStops = build.shipmentStops;
		this.equipmentIdentifiers = build.equipmentIdentifiers;
		this.capacityProviderAccountGroup = build.capacityProviderAccountGroup;
		this.shippingDetails = build.shippingDetails;
		this.apiConfiguration = build.apiConfiguration;
		this.attributes = build.attributes;
	}

	public static class Builder {
		@Autowired
		private CarrierIdentifier carrierIdentifier;
		@Autowired
		private List<ShipmentIdentifier> shipmentIdentifiers;
		@Autowired
		private List<ShipmentStops> shipmentStops;
		@Autowired
		private CapacityProviderAccountGroup capacityProviderAccountGroup;
		@Autowired
		private List<EquipmentIdentifiers> equipmentIdentifiers;
		@Autowired
		private ShippingDetails shippingDetails;
		@Autowired
		private ApiConfiguration apiConfiguration;
		@Autowired
		private List<Attribute> attributes;



		public Builder setCarrierIdentifier(CarrierIdentifier carrierIdentifier) {
			this.carrierIdentifier = carrierIdentifier;
			return this;
		}

		public Builder setShipmentIdentifiers(List<ShipmentIdentifier> shipmentIdentifiers) {
			this.shipmentIdentifiers = shipmentIdentifiers;
			return this;
		}

		public Builder setShipmentStops(List<ShipmentStops> shipmentStops) {
			this.shipmentStops = shipmentStops;
			return this;
		}

		public Builder setCapacityProviderAccountGroup(CapacityProviderAccountGroup capacityProviderAccountGroup) {
			this.capacityProviderAccountGroup = capacityProviderAccountGroup;
			return this;
		}

		public Builder setEquipmentIdentifiers(List<EquipmentIdentifiers> equipmentIdentifiers) {
			this.equipmentIdentifiers = equipmentIdentifiers;
			return this;
		}

		public Builder setShippingDetails(ShippingDetails shippingDetails) {
			this.shippingDetails = shippingDetails;
			return this;
		}

		public Builder setApiConfiguration(ApiConfiguration apiConfiguration) {
			this.apiConfiguration = apiConfiguration;
			return this;
		}

		public Builder setAttributes(List<Attribute> attributes) {
			this.attributes = attributes;
			return this;
		}

		public InitializeTruckLoad build() {
			return new InitializeTruckLoad(this);
		}
	}

	@JsonProperty("id")
	public Integer getId() {
		return id;
	}

	@JsonProperty("carrierIdentifier")
	public CarrierIdentifier getCarrierIdentifier() {
		return carrierIdentifier;
	}

	@JsonProperty("shipmentIdentifiers")
	public List<ShipmentIdentifier> getShipmentIdentifiers() {
		return shipmentIdentifiers;
	}

	@JsonProperty("shipmentStops")
	public List<ShipmentStops> getShipmentStops() {
		return shipmentStops;
	}

	@JsonProperty("capacityProviderAccountGroup")
	public CapacityProviderAccountGroup getCapacityProviderAccountGroup() {
		return capacityProviderAccountGroup;
	}

	@JsonProperty("equipmentIdentifiers")
	public List<EquipmentIdentifiers> getEquipmentIdentifiers() {
		return equipmentIdentifiers;
	}

	@JsonProperty("shippingDetails")
	public ShippingDetails getShippingDetails() {
		return shippingDetails;
	}

	@JsonProperty("apiConfiguration")
	public ApiConfiguration getApiConfiguration() {
		return apiConfiguration;
	}

	@JsonProperty("attributes")
	public List<Attribute> getAttributes() {
		return attributes;
	}
}
