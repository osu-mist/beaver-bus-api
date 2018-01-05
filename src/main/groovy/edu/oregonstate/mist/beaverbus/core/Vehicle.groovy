package edu.oregonstate.mist.beaverbus.core


class VehicleAttributes {
    String routeID // routeID for route of vehicle
    String name // Name of vehicle
    float latitude // Latitude of vehicle's position
    float longitude // Longitude of vehicle's position
    float speed // Speed of vehicle (MPH)
    Integer heading // Compass heading of vehicle (0-359, where 0 is north)
    String lastUpdated // Time at which vehicle's position was updated
    Boolean onRoute // Whether the vehicle is on its route
    Boolean delayed // Whether the vehicle is delayed
}
