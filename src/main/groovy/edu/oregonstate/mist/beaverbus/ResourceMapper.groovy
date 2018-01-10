package edu.oregonstate.mist.beaverbus

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.beaverbus.core.ArrivalAttributes
import edu.oregonstate.mist.beaverbus.core.ArrivalTime
import edu.oregonstate.mist.beaverbus.core.RouteAttributes
import edu.oregonstate.mist.beaverbus.core.Stop
import edu.oregonstate.mist.beaverbus.core.VehicleAttributes
import groovy.transform.CompileStatic

import java.time.Instant
import java.time.temporal.ChronoUnit

@CompileStatic
class ResourceMapper {
    static ResourceObject mapRoute(RouteWithSchedule route, BeaverBusUriBuilder build) {
        new ResourceObject(
                id: route.RouteID.toString(),
                type: "route",
                attributes: new RouteAttributes(
                        description: route.Description,
                        encodedPolyline: route.EncodedPolyline,
                        mapColor: route.MapLineColor,
                        latitude: route.MapLatitude,
                        longitude: route.MapLongitude,
                        zoomLevel: route.MapZoom,
                        stops: route.Stops.toSorted { it.Order }.collect { this.mapStop(it) },
                ),
                links: [
                        self: build.routeUri(route.RouteID)
                        // TODO: add arrivals link?
                        // TODO: add vehicles link?
                ],
        )
    }

    static Stop mapStop(RouteStop stop) {
        new Stop(
                stopID: stop.RouteStopID.toString(), // TODO
                description: stop.Description,
                latitude: stop.Latitude,
                longitude: stop.Longitude,
        )
    }

    static ResourceObject mapVehicle(Vehicle vehicle, BeaverBusUriBuilder build) {
        def lastUpdated = Instant.now().truncatedTo(ChronoUnit.SECONDS)
                .minusSeconds(vehicle.Seconds)
        new ResourceObject(
                id: vehicle.VehicleID.toString(),
                type: "vehicle",
                attributes: new VehicleAttributes(
                        routeID: vehicle.RouteID.toString(),
                        name: vehicle.Name,
                        latitude: vehicle.Latitude,
                        longitude: vehicle.Longitude,
                        speed: vehicle.GroundSpeed,
                        heading: vehicle.Heading,
                        lastUpdated: lastUpdated.toString(),
                        onRoute: vehicle.IsOnRoute,
                        delayed: vehicle.IsDelayed,
                ),
                links: [
                        self: build.vehicleUri(vehicle.VehicleID),
                        route: build.routeUri(vehicle.RouteID), // TODO add to swagger
                ],
        )
    }

    static ResourceObject mapArrival(RouteStopArrival arrival, BeaverBusUriBuilder build) {
        new ResourceObject(
                id: "0", // XXX
                type: "arrival",
                attributes: new ArrivalAttributes(
                        routeID: arrival.RouteID.toString(),
                        stopID: arrival.RouteStopID.toString(),
                        arrivals: arrival.Times.collect { this.mapArrivalTime(it) },
                ),
                links: [
                        self: build.arrivalUri(arrival.RouteID, arrival.RouteStopID),
                        route: build.routeUri(arrival.RouteID),
                ],
        )
    }

    static ArrivalTime mapArrivalTime(RouteStopArrivalTime time) {
        // TODO use Time field
        def eta = Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(time.Seconds)
        new ArrivalTime(
                vehicleID: time.VehicleID.toString(),
                eta: eta.toString(),
        )
    }

    static Instant parseDate(String date) {
    }
}
