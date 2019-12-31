package org.chargingpoint.reservation;

import javax.annotation.PostConstruct;
import javax.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;

@Configuration
@ApplicationPath("/")
public class ReservationConfiguration extends ResourceConfig {
	public ReservationConfiguration() {
	}

	@PostConstruct
	public void setUp() {
		register(Reservation.class);
		register(OpenApiResource.class);
	}
}