package edu.oregonstate.mist.beaverbus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.transform.ToString
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.util.EntityUtils

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
        //println(url.toString())
        def resp = httpClient.execute(new HttpGet(url))
        // XXX check http code?
        // XXX catch IO Exception?
        def body = EntityUtils.toString(resp.entity)
        //println(body)
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
@ToString
class RouteWithSchedule {
    @JsonProperty    Integer RouteID
    @JsonProperty    String Description
    @JsonProperty    String EncodedPolyline
    @JsonProperty    String MapLineColor
    @JsonProperty    Double MapLatitude
    @JsonProperty    Double MapLongitude
    @JsonProperty    Integer MapZoom
    @JsonProperty    List<RouteStop> Stops
    // etc
}

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class RouteStop {
    @JsonProperty    Integer RouteStopID
    @JsonProperty    Integer RouteID
    @JsonProperty    String Description
    @JsonProperty    Double Latitude
    @JsonProperty    Double Longitude
    @JsonProperty    Integer Order
    // etc
}

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class Vehicle {
    @JsonProperty    Integer VehicleID
    @JsonProperty    Integer RouteID
    @JsonProperty    String Name
    @JsonProperty    Double Latitude
    @JsonProperty    Double Longitude
    @JsonProperty    Double GroundSpeed
    @JsonProperty    Integer Heading
    @JsonProperty    Integer Seconds
    @JsonProperty    Boolean IsOnRoute
    @JsonProperty    Boolean IsDelayed
}

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class RouteStopArrival {
    @JsonProperty    Integer RouteID
    @JsonProperty    Integer RouteStopID
    @JsonProperty    List<RouteStopArrivalTime> Times
}

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class RouteStopArrivalTime {
    @JsonProperty    Integer VehicleID
    @JsonProperty    String Text
    @JsonProperty    String Time
    @JsonProperty    Integer Seconds
    @JsonProperty    Boolean IsArriving
    // etc
}