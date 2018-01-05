package edu.oregonstate.mist.beaverbus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet

import javax.ws.rs.core.UriBuilder

@CompileStatic
class RideSystemsDAO {
    HttpClient httpClient
    URL baseURL
    String apiKey

    ObjectMapper mapper = new ObjectMapper()

    List<RouteWithSchedule> getRoutesForMapWithScheduleWithEncodedLine() {
        def url = UriBuilder.fromUri(baseURL.toURI())
                .path("GetRoutesForMapWithScheduleWithEncodedLine")
                .queryParam("ApiKey", apiKey)
                .build()
        def resp = httpClient.execute(new HttpGet(url))
        // XXX check http code?
        // XXX catch IO Exception?
        def body = resp.entity.toString()
        (List<RouteWithSchedule>) mapper.readValue(body, new TypeReference<List<RouteWithSchedule>>(){})
    }

    List<Vehicle> getMapVehiclePoints() {
        // get url...
        def body=""
        (List<Vehicle>) mapper.readValue(body, new TypeReference<List<Vehicle>>(){})
    }

    List<Vehicle> getMapVehiclePointsByRouteID(Integer routeID) {
        def body = ""
        (List<Vehicle>) mapper.readValue(body, new TypeReference<List<Vehicle>>(){})
    }

    Vehicle getMapVehiclePointsByVehicleID(Integer vehicleID) {
        def vehicles = getMapVehiclePoints()
        vehicles.find { v -> v.VehicleID == vehicleID }
    }

    List<RouteStopArrival> getStopArrivalTimes() {
        def body = ""
        (List<RouteStopArrival>) mapper.readValue(body, new TypeReference<List<RouteStopArrival>>(){})
    }

}


// See Ride Systems Web Services documentation for an explanation of these fields

@JsonIgnoreProperties(ignoreUnknown=true)
class RouteWithSchedule {
    Integer RouteID
    String Description
    String EncodedPolyline
    // etc
}


@JsonIgnoreProperties(ignoreUnknown=true)
class Vehicle {
    Integer VehicleID
    Integer RouteID
    String Name
    Double Latitude
    Double Longitude
    Integer Seconds
    // etc
}

@JsonIgnoreProperties(ignoreUnknown=true)
class RouteStopArrival {
    Integer VehicleID
    Integer RouteID
    String Name
    Double Latitude
    Double Longitude
    Integer Seconds
    // etc
}