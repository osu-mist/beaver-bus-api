package edu.oregonstate.mist.beaverbus.core

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


@EqualsAndHashCode
@ToString
class ArrivalAttributes {
    String routeID // routeID for route of vehicle
    String stopID // Unique identifier for a stop
    List<ArrivalTime> arrivals // Times that this route is arriving at this stop
}

@EqualsAndHashCode
@ToString
class ArrivalTime {
    String eta       // Time and date that route is expected to arrive at stop
    String vehicleID // vehicleID of vehicle that is arriving
}
