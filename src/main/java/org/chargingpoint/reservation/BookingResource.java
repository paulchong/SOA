package org.chargingpoint.reservation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.ScanParams;
import redis.clients.jedis.ScanResult;

public class BookingResource {

	public static JedisPool pool = new JedisPool(new JedisPoolConfig(),
			System.getenv().containsKey("REDIS_HOST") ? System.getenv("REDIS_HOST") : "localhost");
	public static String oneuuid = UUID.randomUUID().toString();

	/*
	 * Instantiate the system with a new random booking
	 */
	public BookingResource() {

		BookingRecord record = new BookingRecord(oneuuid, new BookingAttribute("" + "{'chargingPointID':'123abc'," + "'carID':'112233',"
				+ "'duration':'30'," + "'startTimeDate':'21/12/2019 01:30:00 PM'," + "'customerID':'1',"
				+ "'customerFirstName':'Charlie'," + "'customerLastName':'Rose'," + "'endTimeDate':'21/12/2019 02:00:00 PM'}"));
		record.setComplete();
		this.putBookingToDB(oneuuid, record);
	}

	/*
	 * This method will throw a JSONException if there is a problem with the
	 * JSON otherwise it will create a booking and return a new UUID
	 */
	public String createBooking(String booking) {
		String uuid = UUID.randomUUID().toString();
		BookingAttribute bookingAttribute = new BookingAttribute(booking);

		BookingRecord record = new BookingRecord(uuid, bookingAttribute);
		record.setBookingTime(System.currentTimeMillis());
		record.setComplete();

		if (this.isBookingInDB(uuid))
			throw new RuntimeException("Serious UUID problem");
		putBookingToDB(uuid, record);
		return uuid;
	}

	/*
	 * This will upstartTimeDate a booking it can throw NotFoundException if that uuid is
	 * not present It can throw JSONException if the JSON is bad
	 */
	public void upstartTimeDateBooking(String uuid, String booking) throws NotFoundException {
		if (uuid == null || !isBookingInDB(uuid)) {
			throw new NotFoundException();
		}

		BookingRecord record = getBookingFromDB(uuid);
		BookingAttribute bookingAttribute = new BookingAttribute(booking);
		record.setAttribute(bookingAttribute);
		record.setComplete();
		putBookingToDB(uuid, record);

	}

	// this method returns a JSON array of all bookings
	// which may be empty
	// It does not list incomplete or deleted bookings
	public String getBookings() throws JSONException {
		JSONArray array = new JSONArray();
		List<String> keys = getBookingIDs();

		Iterator<String> i = keys.iterator();
		while (i.hasNext()) {
			String uuid = i.next();

			try {
				BookingRecord record = getBookingFromDB(uuid);

				if (record.isComplete() && !record.isDeleted()) {
					JSONObject href = new JSONObject();

					href.put("href", uuid);
					array.put(href);

				}
			} catch (NotFoundException nfe) {
				// serious error here
			}
		}

		JSONObject json = new JSONObject();

		json.put("bookings", array);
		return json.toString(3); // indent 3 for nicer printing!
	}

	/*
	 * This method returns NotFoundException if no booking ever existed null if
	 * the booking has been deleted Otherwise the JSON
	 */
	public String getBooking(String id) throws NotFoundException, JSONException {
		if (!isBookingInDB(id))
			throw new NotFoundException();

		BookingRecord record = getBookingFromDB(id);
		if (record.isDeleted())
			return null;

		else
			return record.getBooking().toJSON().toString();
	}

	/*
	 * This method returns true if freshly deleted false if already deleted
	 * NotFoundException if it never existed
	 */
	public boolean deleteBooking(String id) throws NotFoundException {
		if (isBookingInDB(id)) {
			BookingRecord record = getBookingFromDB(id);
			if (record.isDeleted()) {
				return false;
			}
			record.delete();
			putBookingToDB(id, record);
			return true;
		} else {
			throw new NotFoundException();
		}
	}

	public void putBookingToDB(String uuid, BookingRecord booking) {

		try (Jedis jedis = pool.getResource()) {
			jedis.set(uuid + ":complete", booking.isComplete() ? "true" : "false");
			jedis.set(uuid + ":deleted", booking.isDeleted() ? "true" : "false");
			jedis.set(uuid + ":json", booking.getBooking().toString());
		}
	}

	public BookingRecord getBookingFromDB(String uuid) throws NotFoundException {
		try (Jedis jedis = pool.getResource()) {
			String json = jedis.get(uuid + ":json");
			if (json == null) {
				throw new NotFoundException();
			}

			BookingAttribute booking = new BookingAttribute(json);
			BookingRecord record = new BookingRecord(uuid, booking);
			String complete = jedis.get(uuid + ":complete");
			String deleted = jedis.get(uuid + ":deleted");
			if ("true".equals(complete))
				record.setComplete();
			if ("true".equals(deleted))
				record.delete();
			return record;
		}
	}

	public boolean isBookingInDB(String uuid) {
		try (Jedis jedis = pool.getResource()) {
			return jedis.exists(uuid + ":json");
		}
	}

	public List<String> getBookingIDs() {
		try (Jedis jedis = pool.getResource()) {
			// note this logic does not cope with large sets of responses
			ScanParams params = new ScanParams();
			params.match("*:json");

			// Use "0" to do a full iteration of the collection.
			ScanResult<String> scanResult = jedis.scan("0", params);
			List<String> keys = scanResult.getResult();
			List<String> uuids = new LinkedList<String>();
			Iterator<String> i = keys.iterator();
			while (i.hasNext()) {
				String key = i.next();
				String uuid = key.substring(0, key.indexOf(":json"));
				uuids.add(uuid);
			}
			return uuids;
		}
	}
}
