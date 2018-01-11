package edu.oregonstate.mist.beaverbus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonMappingException
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
        def resp = httpClient.execute(new HttpGet(url))
        // XXX check http code?
        // XXX catch IO Exception?
        def body = EntityUtils.toString(resp.entity)
        try {
            (List<RouteWithSchedule>) mapper.readValue(body,
                    new TypeReference<List<RouteWithSchedule>>() {})
        } catch (JsonMappingException exc) {
            // Catch and wrap the jsonmappingexception.
            // Dropwizard catches and eats any jsonmappingexception thrown during a request and
            // returns an unhelpful error message, probably assuming that the exception was caused
            // by a syntax error in the request body, which is not the case here
            throw new Exception(exc)
        }
    }

    List<Vehicle> getMapVehiclePoints(Integer routeID) {
        def builder = UriBuilder.fromUri(baseURL.toURI()).path("GetMapVehiclePoints")
        builder.queryParam("ApiKey", apiKey)
        if (routeID != null) {
            builder.queryParam("routeID", routeID)
        }
        def url = builder.build()
        def resp = httpClient.execute(new HttpGet(url))
        def body = EntityUtils.toString(resp.entity)

        try {
            (List<Vehicle>) mapper.readValue(body, new TypeReference<List<Vehicle>>() {})
        } catch (JsonMappingException exc) {
            // See previous comments
            throw new Exception(exc)
        }
    }

    List<RouteStopArrival> getStopArrivalTimes(Integer routeID, Integer stopID) {
        def builder = UriBuilder.fromUri(baseURL.toURI()).path("GetStopArrivalTimes")
        builder.queryParam("ApiKey", apiKey)
        if (routeID != null) {
            // TODO: should use builder.queryParam("routeIDs", "{id}") and
            // builder.build(routeID), but that's painful, and this is safe
            // if routeID is an integer
            builder.queryParam("routeIDs", routeID)
        }
        if (stopID != null) {
            builder.queryParam("routeStopIDs", stopID)
        }
        def url = builder.build()
        def resp = httpClient.execute(new HttpGet(url))
        def body = EntityUtils.toString(resp.entity)

        try {
            (List<RouteStopArrival>) mapper.readValue(body,
                    new TypeReference<List<RouteStopArrival>>() {})
        } catch (JsonMappingException exc) {
            // See previous comments
            throw new Exception(exc)
        }
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
    @JsonProperty("RouteId")    Integer RouteID
    @JsonProperty("RouteStopId")    Integer RouteStopID
    @JsonProperty    List<RouteStopArrivalTime> Times
}

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class RouteStopArrivalTime {
    @JsonProperty("VehicleId")    Integer VehicleID
    @JsonProperty    String Text
    @JsonProperty    String Time
    @JsonProperty    Integer Seconds
    @JsonProperty    Boolean IsArriving
    // etc
}