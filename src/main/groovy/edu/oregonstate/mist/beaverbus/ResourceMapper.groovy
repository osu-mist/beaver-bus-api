package edu.oregonstate.mist.beaverbus

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.beaverbus.core.RouteAttributes
import edu.oregonstate.mist.beaverbus.core.VehicleAttributes
import groovy.transform.CompileStatic

@CompileStatic
class ResourceMapper {
    static ResourceObject mapRoute(RouteWithSchedule route) {
        new ResourceObject(
                id: route.RouteID.toString(),
                type: "route",
                attributes: new RouteAttributes(
                        description: route.Description,
                        encodedPolyline: route.EncodedPolyline,
                )
        )
    }

    ResourceObject mapVehicle(Vehicle vehicle) {
        new ResourceObject(
                id: vehicle.VehicleID.toString(),
                type: "vehicle",
                attributes: new VehicleAttributes(

                )
        )
    }
}
