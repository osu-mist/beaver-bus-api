package edu.oregonstate.mist.beaverbus

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.beaverbus.core.ArrivalAttributes
import edu.oregonstate.mist.beaverbus.core.ArrivalTime
import edu.oregonstate.mist.beaverbus.core.RouteAttributes
import edu.oregonstate.mist.beaverbus.core.Stop
import edu.oregonstate.mist.beaverbus.core.VehicleAttributes
import groovy.transform.CompileStatic

import javax.ws.rs.core.UriBuilder
import java.time.Instant
import java.time.temporal.ChronoUnit

@CompileStatic
class ResourceMapper {
    static ResourceObject mapRoute(RouteWithSchedule route, URI endpointUri) {
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
                        stops: route.Stops.toSorted{ it.Order }.collect { this.mapStop(it) },
                ),
                links: [
                        self: UriBuilder.fromUri(endpointUri).path("routes/{id}").build(route.RouteID)
                        // TODO: add arrivals link
                        // TODO: add vehicles link
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

    static ResourceObject mapVehicle(Vehicle vehicle, URI endpointUri) {
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
                        self: UriBuilder.fromUri(endpointUri).path("vehicles/{id}").build(vehicle.VehicleID),
                        route: UriBuilder.fromUri(endpointUri).path("routes/{id}").build(vehicle.RouteID), // TODO add to swagger
                ],
        )
    }

    static ResourceObject mapArrival(RouteStopArrival arrival, URI endpointUri) {
        new ResourceObject(
                id: "0", // XXX
                type: "arrival",
                attributes: new ArrivalAttributes(
                        routeID: arrival.RouteID.toString(),
                        stopID: arrival.RouteStopID.toString(),
                        arrivals: arrival.Times.collect{ this.mapArrivalTime(it) },
                ),
                links: [
                        self: UriBuilder.fromUri(endpointUri).path("arrivals")
                                .queryParam("routeID", "{routeID}")
                                .queryParam("stopID", "{stopID}")
                                .build(arrival.RouteID, arrival.RouteStopID),
                        route: UriBuilder.fromUri(endpointUri).path("routes/{id}")
                                .build(arrival.RouteID),
                ],
        )
    }

    static ArrivalTime mapArrivalTime(RouteStopArrivalTime time) {
        new ArrivalTime(
                vehicleID: time.VehicleID.toString(),
                eta: Instant.now().truncatedTo(ChronoUnit.SECONDS).plusSeconds(time.Seconds).toString(), // TODO use Time field
        )
    }

    static Instant parseDate(String date) {
    }
}
