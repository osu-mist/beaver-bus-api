package edu.oregonstate.mist.beaverbus

import com.fasterxml.jackson.annotation.JsonProperty
import edu.oregonstate.mist.api.Configuration
import io.dropwizard.client.HttpClientConfiguration

import javax.validation.constraints.NotNull

class BeaverBusConfiguration extends Configuration {
    @JsonProperty("ridesystems")
    @NotNull
    RideSystemsConfiguration rideSystems

    HttpClientConfiguration httpClient
}

class RideSystemsConfiguration {
    URL baseURL
    String apiKey
}
