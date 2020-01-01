package org.chargingpoint.reservation;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.json.JSONException;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@Component
@Path("/reservation")
@OpenAPIDefinition (info = @Info (
	title = "ReservationAPI", version = "0.0.1"
))

public class Reservation {

	BookingResource backend = new BookingResource();
	// Publish publisher = new Publish();

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createBooking(String input, @Context UriInfo uriInfo) {

		boolean success = true;
		String bookingId = null;
		try {
			bookingId = backend.createBooking(input);
			// publisher.publish(input);
		} catch (JSONException je) {
			success = false;
		}
		catch (Exception ie) {
			ie.printStackTrace();
		}

		if (success) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			builder.path(bookingId);
			try {
				return Response.created(builder.build()).entity(backend.getBooking(bookingId)).build();
			} catch (IllegalArgumentException | UriBuilderException | JSONException | NotFoundException e) {
				// something really freaky happened here
				return Response.serverError().build();
			}
		}
		return Response.status(Status.BAD_REQUEST).build();
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response upstartTimeDateBooking(String input, @PathParam("id") String id) {
		try {
			backend.upstartTimeDateBooking(id, input);
			// return the server's representation
			return Response.ok(backend.getBooking(id)).build();
		} catch (JSONException je) {
			return Response.status(Status.BAD_REQUEST).build();
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response getBooking(@PathParam("id") String id)
	{
		    String bookingJSON;
			try {
				bookingJSON = backend.getBooking(id);
			} catch ( NotFoundException e) {
				return Response.status(Status.NOT_FOUND).build();
			}
			if (bookingJSON == null) {
				return Response.status(Status.GONE).build();
			}
			return Response.ok(bookingJSON).build();
	
	}
	@DELETE
	@Path("{id}")
	public Response deleteBooking(@PathParam("id") String id)
	{
		   try {
				boolean deleted = backend.deleteBooking(id);
				if (deleted) {
					return Response.ok().build();
				}
				else
				{
					return Response.status(Status.GONE).build();
				}
			} catch ( NotFoundException e) {
				return Response.status(Status.NOT_FOUND).build();
			}
			
	}
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllBookings()
	{
		String allBookings = backend.getBookings();
		return Response.ok().entity(allBookings).build();
	}
}