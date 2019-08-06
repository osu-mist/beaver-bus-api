package edu.oregonstate.mist.beaverbus

import com.codahale.metrics.annotation.Timed
import edu.oregonstate.mist.api.Resource
import edu.oregonstate.mist.api.jsonapi.ResultObject
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.annotation.security.PermitAll
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("beaverbus")
@Produces(MediaType.APPLICATION_JSON)
@PermitAll
@CompileStatic
class BeaverBusResource extends Resource {
    private final RideSystemsDAO rideSystemsDAO
    private final ResourceMapper mapper = new ResourceMapper()
    private BeaverBusUriBuilder uriBuilder
    private static Logger logger = LoggerFactory.getLogger(this.class)

    BeaverBusResource(RideSystemsDAO rideSystemsDAO, URI endpointUri) {
        this.rideSystemsDAO = rideSystemsDAO
        this.endpointUri = endpointUri
        this.uriBuilder = new BeaverBusUriBuilder(endpointUri)
    }

    /**
     * Attempts to run closure and handles a RideSystemException when necessary
     *
     * @param closure The closure to run
     * @return Response from closure or error response
     */
    static Response errorHandler(Closure<Response> closure) {
        try {
            closure()
        } catch (RideSystemsException exc) {
            String message = exc.message
            logger.error(message)
            if (message.contains('non-200 status 503')) {
                return serviceUnavailable().build()
            }
            internalServerError('unexpected response from data source').build()
        }
    }

    @Path("routes")
    @GET
    @Timed
    Response getRoutes() {
        errorHandler { ->
            def routes = rideSystemsDAO.getRoutesForMapWithScheduleWithEncodedLine()
            def routeResources = routes.collect { mapper.mapRoute(it, uriBuilder) }
            ok(new ResultObject(data: routeResources)).build()
        }
    }

    @Path("routes/{id}")
    @GET
    @Timed
    Response getSingleRoute(@PathParam("id") Integer id) {
        errorHandler { ->
            /*
             * RideSystems does not supply any way to get just a single route, so we have to fetch
             * them all and then filter out the one we want
             */
            def routes = rideSystemsDAO.getRoutesForMapWithScheduleWithEncodedLine()
            def route = routes.find { it.RouteID == id }
            if (route == null) {
                return notFound().build()
            }
            def resource = mapper.mapRoute(route, uriBuilder)
            ok(new ResultObject(data: resource)).build()
        }
    }

    @Path("vehicles")
    @GET
    @Timed
    Response getVehicles(@QueryParam('routeID') Integer routeID) {
        errorHandler { ->
            def vehicles = rideSystemsDAO.getMapVehiclePoints(routeID)
            def vehicleResources = vehicles.collect { mapper.mapVehicle(it, uriBuilder) }
            ok(new ResultObject(data: vehicleResources)).build()
        }
    }

    @Path("vehicles/{id}")
    @GET
    @Timed
    Response getSingleVehicle(@PathParam("id") Integer id) {
        errorHandler { ->
            /*
             * RideSystems does not supply any way to get just a single vehicle, so we have to fetch
             * them all and then filter out the one we want
             */
            def vehicles = rideSystemsDAO.getMapVehiclePoints(null)
            def vehicle = vehicles.find { it.VehicleID == id }
            if (vehicle == null) {
                return notFound().build()
            }
            def resource = mapper.mapVehicle(vehicle, uriBuilder)
            ok(new ResultObject(data: resource)).build()
        }
    }

    @Path("arrivals")
    @GET
    @Timed
    Response getArrivals(
            @QueryParam("routeID") Integer routeID,
            @QueryParam("stopID") Integer stopID
    ) {
        errorHandler { ->
            def arrivals = rideSystemsDAO.getStopArrivalTimes(routeID, stopID)
            def arrivalResources = arrivals.collect { mapper.mapArrival(it, uriBuilder) }
            ok(new ResultObject(data: arrivalResources)).build()
        }
    }
}