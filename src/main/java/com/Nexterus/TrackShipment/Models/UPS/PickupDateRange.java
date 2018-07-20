package com.Nexterus.TrackShipment.Models.UPS;

import java.sql.Timestamp;
import java.util.Calendar;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

@Component
public class PickupDateRange {

	private String beginDate;
	private String endDate;

	public PickupDateRange() {

		Timestamp futureDay = new Timestamp(System.currentTimeMillis());
		String endPkUpDate = futureDay.toString();
		endPkUpDate = endPkUpDate.substring(0, 10);
		endPkUpDate = endPkUpDate.replace("-", "");
		System.out.println("End PkUpDate Date " + endPkUpDate);
		setEndDate(endPkUpDate);

		String beginPkUpDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(futureDay);
		cal.add(Calendar.DAY_OF_WEEK, -14);
		futureDay.setTime(cal.getTime().getTime());
		beginPkUpDate = futureDay.toString();
		beginPkUpDate = beginPkUpDate.substring(0, 10);
		beginPkUpDate = beginPkUpDate.replace("-", "");
		System.out.println("Begin PkUpDate Date " + beginPkUpDate);
		setBeginDate(beginPkUpDate);
	}

	public PickupDateRange(String older) {

		Timestamp futureDay = new Timestamp(System.currentTimeMillis());
		String endPkUpDate = futureDay.toString();
		endPkUpDate = endPkUpDate.substring(0, 10);
		endPkUpDate = endPkUpDate.replace("-", "");
		System.out.println("End PkUpDate Date " + endPkUpDate);
		setEndDate(endPkUpDate);

		String beginPkUpDate = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(futureDay);
		cal.add(Calendar.DAY_OF_WEEK, -28);
		futureDay.setTime(cal.getTime().getTime());
		beginPkUpDate = futureDay.toString();
		beginPkUpDate = beginPkUpDate.substring(0, 10);
		beginPkUpDate = beginPkUpDate.replace("-", "");
		System.out.println("Begin PkUpDate Date " + beginPkUpDate);
		setBeginDate(beginPkUpDate);
	}

	@JsonProperty("BeginDate")
	public String getBeginDate() {
		return beginDate;
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
	}

	@JsonProperty("EndDate")
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

}
