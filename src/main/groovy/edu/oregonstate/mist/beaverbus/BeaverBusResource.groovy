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
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("beaverbus")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@CompileStatic
class BeaverBusResource extends Resource {
    private final RideSystemsDAO rideSystemsDAO
    private final ResourceMapper mapper = new ResourceMapper()

    BeaverBusResource(RideSystemsDAO rideSystemsDAO, URI endpointUri) {
        this.rideSystemsDAO = rideSystemsDAO
        this.endpointUri = endpointUri
    }

    @Path("routes")
    @GET
    @Timed
    Response getRoutes() {
        def routes = rideSystemsDAO.getRoutesForMapWithScheduleWithEncodedLine()
        def routeResources = routes.collect{ mapper.mapRoute(it) }
        def result = new ResultObject(data: routeResources)
        ok(result).build()
    }

    @Path("routes/{id}")
    @GET
    @Timed
    Response getSingleRoute(@PathParam("id") Integer id) {
        def routes = rideSystemsDAO.getRoutesForMapWithScheduleWithEncodedLine()
        def route = routes.find{ it.RouteID == id }
        if (route == null) {
            return notFound().build()
        }

        def resource = mapper.mapRoute(route)
        ok(resource).build()
    }

    @Path("vehicles")
    @GET
    @Timed
    Response getVehicles() {
        def vehicles = rideSystemsDAO.getMapVehiclePoints()
        def vehicleResources = vehicles.collect{ mapper.mapVehicle(it) }
        def result = new ResultObject(data: vehicleResources)
        ok(result).build()
    }

    @Path("vehicles/{id}")
    @GET
    @Timed
    Response getSingleVehicle(@PathParam("id") Integer id) {
        def vehicles = rideSystemsDAO.getMapVehiclePoints()
        def vehicle = vehicles.find{ it.VehicleID == id }
        if (vehicle == null) {
            return notFound().build()
        }
        def resource = mapper.mapVehicle(vehicle)
        ok(resource).build()
    }

    @Path("arrivals")
    @GET
    @Timed
    Response getArrivals() {
        def arrivals = rideSystemsDAO.getStopArrivalTimes()
        def arrivalResources = arrivals.collect{ mapper.mapArrival(it) }
        def result = new ResultObject(data: arrivalResources)
        ok(result).build()
    }
}