package edu.oregonstate.mist.beaverbus.core

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class RouteAttributes {
    String description // Description of bus route
    String encodedPolyline // Encoded polyline for mapping route using Google's mapping APIs
    String mapColor // Hexadecimal color value for route line on map
    Double latitude // Default latitude to go to when selecting route on map
    Double longitude // Default longitude to go to when selecting route on map
    Integer zoomLevel // Default zoom level to use when selecting route on map
    List<Stop> stops // Locations where the bus stops along the route. Stops are in the order at which they are arrived at.
}

@EqualsAndHashCode
@ToString
class Stop {
    String stopID
    String description
    Double latitude // Latitude of stop
    Double longitude // Longitude of stop
}
