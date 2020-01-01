package org.chargingpoint.reservation;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class BookingAttribute {
	private String chargingPointID;
	private String carID;
	private String duration;
	private String startTimeDate;
	private String customerID;
	private String endTimeDate;
	private String customerFirstName;
	private String customerLastName;
	
	
	public String getChargingPointID() {
		return chargingPointID;
	}

	public void setChargingPointID(String chargingPointID) {
		this.chargingPointID = chargingPointID;
	}

	public String getCarID() {
		return carID;
	}

	public void setCarID(String carID) {
		this.carID = carID;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getStartTimeDate() {
		return startTimeDate;
	}

	public void setStartTimeDate(String startTimeDate) {
		this.startTimeDate = startTimeDate;
	}

	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public void setEndTimeDate(String endTimeDate) {
		this.endTimeDate = endTimeDate;
	}

	public String getEndTimeDate() {
		return endTimeDate;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}
	
	public BookingAttribute() {
	
	}

	public BookingAttribute(String json) throws JSONException {
		
			JSONObject j = new JSONObject(new JSONTokener(json));
			this.setCustomerID( j.getString("customerID"));
			this.setStartTimeDate(j.getString("startTimeDate"));
			this.setDuration(j.getString("duration"));
			this.setChargingPointID(j.getString("chargingPointID"));
			this.setCarID(j.getString("carID"));
			this.setEndTimeDate(j.getString("endTimeDate"));
			this.setCustomerFirstName(j.getString("customerFirstName"));
			this.setCustomerLastName(j.getString("customerLastName"));
	}

	public JSONObject toJSON() {
		JSONObject j = new JSONObject();
		try {
			j.put("customerID", this.getCustomerID());
			j.put("carID", this.getCarID());
			j.put("duration", this.getDuration());
			j.put("startTimeDate", this.getStartTimeDate());
			j.put("chargingPointID", this.getChargingPointID());
			j.put("endTimeDate", this.getEndTimeDate());
			j.put("customerFirstName", this.getCustomerFirstName());
			j.put("customerLastName", this.getCustomerLastName());
			return j;
		} catch (JSONException je) {
			je.printStackTrace();
		}
		return null;
	}

	public String toString() {
		return this.toJSON().toString();
	}
}
