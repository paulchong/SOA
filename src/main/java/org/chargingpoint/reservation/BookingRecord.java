package org.chargingpoint.reservation;

public class BookingRecord {
	private long bookingTime = 0L;
	private String id = null;
	private BookingAttribute booking = null;
	private boolean complete = false;
	private boolean deleted = false;
	
	
	public BookingRecord(String uuid, BookingAttribute booking) {
		super();
		this.id = uuid;
		this.booking = booking;
		this.complete = false;
		this.bookingTime = System.currentTimeMillis();
		
	}
	public BookingRecord() {
		super();
	}
	
	public void setBookingTime(long now) {
		this.bookingTime = now;
	}

	public void setID(String uuid) {
		this.id = uuid;
	}

	public long getBookingTime() {
		return bookingTime;
	}
	public String getID() {
		return id;
	}
	public void setBean(BookingAttribute booking) {
		this.booking = booking;
	}

	public void setIncomplete() {
		complete = false;
	}
	public void setComplete() {
		complete = true;
	}
	
	public void delete() {
		deleted = true;
	}
	
	public boolean isComplete() {
		return complete;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public BookingAttribute getBooking() {
		return booking;
	}

}
