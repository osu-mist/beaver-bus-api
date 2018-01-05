package edu.oregonstate.mist.beaverbus.core


/*  ArrivalResourceObject:

      links:
        properties:
          route:
            type: string
            format: url
            description: Link to route resource associated with arrival
          self:
            type: string
            format: url
            description: Self link of arrival in format /arrivals?routeID=x&stopID=y

    */
class ArrivalResourceObject {
}

class ArrivalAttributes {
    String routeID // routeID for route of vehicle
    String stopID // Unique identifier for a stop
    List<ArrivalTime> arrivals // Times that this route is arriving at this stop
}

class ArrivalTime {
    String eta       // Time and date that route is expected to arrive at stop
    String vehicleID // vehicleID of vehicle that is arriving
}
