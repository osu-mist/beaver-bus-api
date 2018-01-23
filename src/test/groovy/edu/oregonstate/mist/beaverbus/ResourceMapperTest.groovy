package edu.oregonstate.mist.beaverbus

import edu.oregonstate.mist.api.jsonapi.ResourceObject
import edu.oregonstate.mist.beaverbus.core.RouteAttributes
import edu.oregonstate.mist.beaverbus.core.Stop
import groovy.transform.CompileStatic
import groovy.test.GroovyAssert
import org.junit.Test

import java.time.Instant

@CompileStatic
class ResourceMapperTest {
    ResourceMapper mapper = new ResourceMapper()
    BeaverBusUriBuilder uriBuilder = new BeaverBusUriBuilder(new URI("http://example.com/"))

    @Test
    void testMapRoute() {
        def input = new RouteWithSchedule(
                "Description": "West Route 2",
                "EncodedPolyline": "q__oGjlmoV?a@?eC?i@?mA?Y??V@`@AV?HAD?FAJCHC~@Y??NGTKDAFADAH?LAhA?|@?j@?d@?H?@?B?D@F@NBJ?^?^???nC?LD???xA?`F?fABT?N??@FBRBRDRFTDR??HTDJHJFH???@HHf@d@HHJFHDJDJBNBL@B?V@N?FAFAHAFEJIFIHMfAeBb@m@DEDCFAPC??FYBS???CBS@U@O?Q?O?OAM?OAOCQAOAKAIAG??Z?`@Ad@?`@?D?B?@AB?BABAZS??\\~@XbAJb@Np@Hh@Df@Db@BPBb@@JB^@\\?v@AnCCnC??}@@i@AM???sB?aB?gBAwC?m@Ae@?w@?_AA}@?K?cC?A?I?C?KOCCAAECEAEAICG?O?i@?kAAeB?O???wE????gB?gA?gA?g@?m@?sF",
                "MapLatitude": 44.560947 as Double,
                "MapLineColor": "#8c702d",
                "MapLongitude": -123.282271 as Double,
                "MapZoom": 15,
                "RouteID": 19,
                "Stops": [
                        new RouteStop(
                                "Latitude": 44.5645297213 as Double,
                                "Longitude": -123.2815022394 as Double,
                                "Description": "Buxton Hall",
                                "Order": 1,
                                "RouteID": 19,
                                "RouteStopID": 210,
                        ),
                        new RouteStop(
                                "Latitude": 44.5636029495 as Double,
                                "Longitude": -123.2797954233 as Double,
                                "Description": "Weatherford Hall",
                                "Order": 2,
                                "RouteID": 19,
                                "RouteStopID": 211,
                        )
                ]
        )

        def expected = new ResourceObject(
                "id": "19",
                "type": "route",
                "attributes": new RouteAttributes(
                        "description": "West Route 2",
                        "encodedPolyline": "q__oGjlmoV?a@?eC?i@?mA?Y??V@`@AV?HAD?FAJCHC~@Y??NGTKDAFADAH?LAhA?|@?j@?d@?H?@?B?D@F@NBJ?^?^???nC?LD???xA?`F?fABT?N??@FBRBRDRFTDR??HTDJHJFH???@HHf@d@HHJFHDJDJBNBL@B?V@N?FAFAHAFEJIFIHMfAeBb@m@DEDCFAPC??FYBS???CBS@U@O?Q?O?OAM?OAOCQAOAKAIAG??Z?`@Ad@?`@?D?B?@AB?BABAZS??\\~@XbAJb@Np@Hh@Df@Db@BPBb@@JB^@\\?v@AnCCnC??}@@i@AM???sB?aB?gBAwC?m@Ae@?w@?_AA}@?K?cC?A?I?C?KOCCAAECEAEAICG?O?i@?kAAeB?O???wE????gB?gA?gA?g@?m@?sF",
                        "mapColor": "#8c702d",
                        "latitude": 44.560947 as Double,
                        "longitude": -123.282271 as Double,
                        "zoomLevel": 15,
                        "stops": [
                                new Stop(
                                        "stopID": "210",
                                        "description": "Buxton Hall",
                                        "latitude": 44.5645297213 as Double,
                                        "longitude": -123.2815022394 as Double,
                                ),
                                new Stop(
                                        "stopID": "211",
                                        "description": "Weatherford Hall",
                                        "latitude": 44.5636029495 as Double,
                                        "longitude": -123.2797954233 as Double,
                                ),
                        ]
                ),
                "links": [
                        "self": new URI("http://example.com/beaverbus/routes/19"),
                ]
        )

        def actual = mapper.mapRoute(input, uriBuilder)
        assert actual == expected
    }

    @Test
    void testParseDate() {
        GroovyAssert.shouldFail { ResourceMapper.parseDate("") }
        GroovyAssert.shouldFail { ResourceMapper.parseDate("Date(0)") }
        GroovyAssert.shouldFail { ResourceMapper.parseDate("/Date(xxx)/") }
        GroovyAssert.shouldFail { ResourceMapper.parseDate("xxx/Date(1)/xxx") }
        assert ResourceMapper.parseDate("/Date(0)/") == Instant.ofEpochMilli(0)
        assert ResourceMapper.parseDate("/Date(0)/").toString() == "1970-01-01T00:00:00Z"
        assert ResourceMapper.parseDate("/Date(946684800000)/") == Instant.ofEpochMilli(946684800000)
        assert ResourceMapper.parseDate("/Date(946684800000)/").toString() == "2000-01-01T00:00:00Z"
    }
}