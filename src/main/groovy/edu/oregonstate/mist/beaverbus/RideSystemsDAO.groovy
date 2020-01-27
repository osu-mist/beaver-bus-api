package edu.oregonstate.mist.beaverbus

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors
import groovy.transform.ToString
import org.apache.http.HttpResponse
import org.apache.http.HttpStatus
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.ws.rs.core.UriBuilder

@InheritConstructors
class RideSystemsException extends Exception {}

@CompileStatic
class RideSystemsDAO {
    CloseableHttpClient httpClient
    URL baseURL
    String apiKey

    ObjectMapper mapper = new ObjectMapper()

    private static Logger logger = LoggerFactory.getLogger(RideSystemsDAO.class)

    List<RouteWithSchedule> getRoutesForMapWithScheduleWithEncodedLine() {
        // XXX catch IO Exception?
        def body = this.getResponse("GetRoutesForMapWithScheduleWithEncodedLine", [:])

        if (body == "") {
            throw new RideSystemsException("empty response received")
        }

        def routes
        try {
            routes = (List<RouteWithSchedule>) mapper.readValue(body,
                    new TypeReference<List<RouteWithSchedule>>() {})
        } catch (JsonMappingException exc) {
            // Catch and wrap the jsonmappingexception.
            // Dropwizard catches and eats any jsonmappingexception thrown during a request and
            // returns an unhelpful error message, probably assuming that the exception was caused
            // by a syntax error in the request body, which is not the case here
            throw new RideSystemsException(exc)
        }

        // RideSystems responds to a request with an invalid API key
        // by returning a fake route with this error message embedded in it
        // -- but only for this particular endpoint;
        // other endpoints just return an empty response. Good API.
        if (routes.size() == 1 && routes[0].Description == "Unauthorized, contact app developer") {
            throw new RideSystemsException("ridesystems api key is invalid, probably")
        }

        routes
    }

    List<Vehicle> getMapVehiclePoints(Integer routeID) {
        def body = this.getResponse("GetMapVehiclePoints", [
                "routeID": routeID,
        ])

        // This is how RideSystems usually responds to an invalid API key.
        if (body == "") {
            throw new RideSystemsException("response empty; api key probably invalid")
        }

        try {
            (List<Vehicle>) mapper.readValue(body, new TypeReference<List<Vehicle>>() {})
        } catch (JsonMappingException exc) {
            // See comment in getRoutesForMapWithScheduleWithEncodedLine
            throw new RideSystemsException(exc)
        }
    }

    List<RouteStopArrival> getStopArrivalTimes(Integer routeID, Integer stopID) {
        def body = getResponse("GetStopArrivalTimes", [
                "routeIDs": routeID,
                "routeStopIDs": stopID,
        ])

        // This is how RideSystems usually responds to an invalid API key.
        if (body == "") {
            throw new RideSystemsException("response empty; api key probably invalid")
        }

        try {
            (List<RouteStopArrival>) mapper.readValue(body,
                    new TypeReference<List<RouteStopArrival>>() {})
        } catch (JsonMappingException exc) {
            // See comment in getRoutesForMapWithScheduleWithEncodedLine
            throw new RideSystemsException(exc)
        }
    }

    /**
     * GetResponse executes an API call with the given endpoint and query parameters
     * @param endpoint  endpoint name
     * @param params    map of query parameters
     * @throws RideSystemsException
     * @throws IOException  if problem consuming HttpEntity
     * @return          response body as a string
     */
    private String getResponse(String endpoint, Map params) {
        def builder = UriBuilder.fromUri(baseURL.toURI())
        builder.path(endpoint)

        params.each { k, v ->
            if (v != null) {
                builder.queryParam(k as String, "{value}")
                builder.resolveTemplate("value", v)
            }
        }
        builder.queryParam("ApiKey", "{apiKey}")
        builder.resolveTemplate("apiKey", apiKey)

        URI uri = builder.build()
        HttpGet get = new HttpGet(uri)
        logger.debug(get.toString())
        HttpResponse res = httpClient.execute(get)

        int rc = res.getStatusLine().getStatusCode()
        if (rc != HttpStatus.SC_OK) {
            /*
             * RideSystems always returns 200. If we get another status code here, the service is
             * unavailable or the URL is wrong
             */
            throw new RideSystemsException("non-200 status ${rc} returned from ${uri}")
        }

        String body = res.getEntity().getContent().getText()
        EntityUtils.consume(res.getEntity())
        body
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
    @JsonProperty("RouteId")        Integer RouteID
    @JsonProperty("RouteStopId")    Integer RouteStopID
    @JsonProperty                   List<RouteStopArrivalTime> Times
}

@JsonIgnoreProperties(ignoreUnknown=true)
@ToString
class RouteStopArrivalTime {
    @JsonProperty("VehicleId")  Integer VehicleID
    @JsonProperty               String Text
    @JsonProperty               String Time
    @JsonProperty               Integer Seconds
    @JsonProperty               Boolean IsArriving
    // etc
}