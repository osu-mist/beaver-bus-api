package edu.oregonstate.mist.beaverbus

import edu.oregonstate.mist.api.Application
import groovy.transform.CompileStatic
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.setup.Environment
import org.apache.http.client.HttpClient

/**
 * Main application class.
 */
@CompileStatic
class BeaverBusApplication extends Application<BeaverBusConfiguration> {
    /**
     * Parses command-line arguments and runs the application.
     *
     * @param configuration
     * @param environment
     */
    @Override
    public void run(BeaverBusConfiguration configuration, Environment environment) {
        this.setup(configuration, environment)

        def httpClientBuilder = new HttpClientBuilder(environment)
        if (configuration.httpClient != null) {
            httpClientBuilder.using(configuration.httpClient)
        }

        def rideSystemsDAO = new RideSystemsDAO(
                httpClient: httpClientBuilder.build("backend-http-client"),
                baseURL: configuration.rideSystems.baseURL,
                apiKey: configuration.rideSystems.apiKey,
        )

        // TODO: add health check

        def endpointUri = configuration.api.endpointUri
        environment.jersey().register(new BeaverBusResource(rideSystemsDAO, endpointUri))
    }

    /**
     * Instantiates the application class with command-line arguments.
     *
     * @param arguments
     * @throws Exception
     */
    public static void main(String[] arguments) throws Exception {
        new BeaverBusApplication().run(arguments)
    }
}
