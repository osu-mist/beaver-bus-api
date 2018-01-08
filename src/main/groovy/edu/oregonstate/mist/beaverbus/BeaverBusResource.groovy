package edu.oregonstate.mist.beaverbus

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResultObject
import groovy.transform.CompileStatic

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriBuilder

@Path("beaverbus")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@CompileStatic
class BeaverBusResource extends Resource {
    private final RideSystemsDAO rideSystemsDAO
    private final ResourceMapper mapper = new ResourceMapper()
    private URI myEndpointUri

    BeaverBusResource(RideSystemsDAO rideSystemsDAO, URI endpointUri) {
        this.rideSystemsDAO = rideSystemsDAO
        this.endpointUri = endpointUri
        this.myEndpointUri = endpointUri
    }

    @Path("routes")
    @GET
    @Timed
    Response getRoutes() {
        def routes = rideSystemsDAO.getRoutesForMapWithScheduleWithEncodedLine()
        def routeResources = routes.collect{ mapper.mapRoute(it, myEndpointUri) }
        def selfLink = UriBuilder.fromUri(myEndpointUri).path("routes").build()
        def result = new ResultObject(
                data: routeResources,
                links: [self: selfLink],
        )
        ok(result).build()
    }

    @Path("routes/{id}")
    @GET
    @Timed
    Response getSingleRoute(@PathParam("id") Integer id) {
        // RideSystems does not supply any way to get just a single route,
        // so we have to fetch them all and then filter out the one we want
        def routes = rideSystemsDAO.getRoutesForMapWithScheduleWithEncodedLine()
        def route = routes.find{ it.RouteID == id }
        if (route == null) {
            return notFound().build()
        }

        def resource = mapper.mapRoute(route, myEndpointUri)
        ok(resource).build()
    }

    @Path("vehicles")
    @GET
    @Timed
    Response getVehicles(@QueryParam('routeID') Integer routeID) {
        def vehicles = rideSystemsDAO.getMapVehiclePoints(routeID)
        def vehicleResources = vehicles.collect{ mapper.mapVehicle(it, myEndpointUri) }
        def selfLink = UriBuilder.fromUri(myEndpointUri).path("vehicles").build()
        def result = new ResultObject(
                data: vehicleResources,
                links: [self: selfLink],
        )
        ok(result).build()
    }

    @Path("vehicles/{id}")
    @GET
    @Timed
    Response getSingleVehicle(@PathParam("id") Integer id) {
        // RideSystems does not supply any way to get just a single vehicle,
        // so we have to fetch them all and then filter out the one we want
        def vehicles = rideSystemsDAO.getMapVehiclePoints(null)
        def vehicle = vehicles.find{ it.VehicleID == id }
        if (vehicle == null) {
            return notFound().build()
        }
        def resource = mapper.mapVehicle(vehicle, myEndpointUri)
        ok(resource).build()
    }

    @Path("arrivals")
    @GET
    @Timed
    Response getArrivals(
            @QueryParam("routeID") Integer routeID,
            @QueryParam("stopID") Integer stopID
    ) {
        def arrivals = rideSystemsDAO.getStopArrivalTimes(routeID, stopID)
        def arrivalResources = arrivals.collect{ mapper.mapArrival(it, myEndpointUri) }
        def selfLink = UriBuilder.fromUri(myEndpointUri).path("arrivals").build()
        def result = new ResultObject(
                data: arrivalResources,
                links: [self: selfLink],
        )
        ok(result).build()
    }
}