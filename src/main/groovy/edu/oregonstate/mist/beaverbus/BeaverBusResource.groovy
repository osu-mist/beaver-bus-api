package edu.oregonstate.mist.beaverbus

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import groovy.transform.CompileStatic

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("beaverbus")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@CompileStatic
class BeaverBusResource extends Resource {
    private final RideSystemsDAO rideSystemsDAO

    BeaverBusResource(RideSystemsDAO rideSystemsDAO, URI endpointUri) {
        this.rideSystemsDAO = rideSystemsDAO
        this.endpointUri = endpointUri
    }

    @Path("routes")
    @GET
    @Timed
    Resource getRoutes() {

    }

    @Path("routes/{id}")
    @GET
    @Timed
    Response getSingleRoute(@PathParam("id") Integer id) {

    }

    @Path("vehicles")
    @GET
    @Timed
    Response getVehicles() {

    }

    @Path("vehicles/{id}")
    @GET
    @Timed
    Response getSingleVehicle(@PathParam("id") Integer id) {

    }

    @Path("arrivals")
    @GET
    @Timed
    Response getArrivals() {

    }

}