package org.freo.purchase;

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

@Component
@Path("/purchase")
public class Purchase {

	OrderRedis backend = new OrderRedis();
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createOrder(String input, @Context UriInfo uriInfo) {

		boolean success = true;
		String orderId = null;
		try {
			orderId = backend.createOrder(input);
		} catch (JSONException je) {
			success = false;
		}

		if (success) {
			UriBuilder builder = uriInfo.getAbsolutePathBuilder();
			builder.path(orderId);
			try {
				return Response.created(builder.build()).entity(backend.getOrder(orderId)).build();
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
	public Response updateOrder(String input, @PathParam("id") String id) {



		try {
			backend.updateOrder(id, input);
			// return the server's representation
			return Response.ok(backend.getOrder(id)).build();
		} catch (JSONException je) {
			return Response.status(Status.BAD_REQUEST).build();
		} catch (NotFoundException nfe) {
			return Response.status(Status.NOT_FOUND).build();
		}
		
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	public Response getOrder(@PathParam("id") String id)
	{
		    String orderJSON;
			try {
				orderJSON = backend.getOrder(id);
			} catch ( NotFoundException e) {
				return Response.status(Status.NOT_FOUND).build();
			}
			if (orderJSON == null) {
				return Response.status(Status.GONE).build();
			}
			return Response.ok(orderJSON).build();
	
	}
	@DELETE
	@Path("{id}")
	public Response deleteOrder(@PathParam("id") String id)
	{
		   try {
				boolean deleted = backend.deleteOrder(id);
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
	public Response getAllOrders()
	{
		String allOrders = backend.getOrders();
		return Response.ok().entity(allOrders).build();
	}
}