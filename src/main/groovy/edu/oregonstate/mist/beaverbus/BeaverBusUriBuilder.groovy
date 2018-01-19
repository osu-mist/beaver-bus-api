package edu.oregonstate.mist.beaverbus

import javax.ws.rs.core.UriBuilder

class BeaverBusUriBuilder {
    URI endpointUri

    BeaverBusUriBuilder(URI endpointUri) {
        this.endpointUri = endpointUri
    }

    URI routeUri(Integer id) {
        UriBuilder.fromUri(this.endpointUri)
                .path("beaverbus/routes/{id}")
                .build(id)
    }

    URI vehicleUri(Integer id) {
        UriBuilder.fromUri(this.endpointUri)
                .path("beaverbus/vehicles/{id}")
                .build(id)
    }

    URI arrivalUri(Integer routeID, Integer stopID) {
        UriBuilder.fromUri(endpointUri)
                .path("beaverbus/arrivals")
                .queryParam("routeID", "{routeID}")
                .queryParam("stopID", "{stopID}")
                .build(routeID, stopID)
    }
}
