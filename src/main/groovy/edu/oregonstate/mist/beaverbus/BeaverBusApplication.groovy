package edu.oregonstate.mist.beaverbus

import edu.oregonstate.mist.api.Application
import groovy.transform.CompileStatic
import io.dropwizard.client.HttpClientBuilder
import io.dropwizard.setup.Environment
import org.apache.http.conn.ssl.DefaultHostnameVerifier

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

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

        httpClientBuilder.using(new HostnameVerifier() {
            HostnameVerifier verifier = new DefaultHostnameVerifier()
            @Override
            boolean verify(String s, SSLSession sslSession) {
                // We use a custom domain for the RideSystems API,
                // but the certificate is only valid for ridesystems.net, *.ridesystems.net.
                // As a workaround, accept any certificate which is valid for ridesystems.net
                // as valid for any domain name
                return verifier.verify(s, sslSession) || verifier.verify("ridesystems.net", sslSession)
            }
        })

        def rideSystemsDAO = new RideSystemsDAO(
                httpClient: httpClientBuilder.build("backend-http-client"),
                baseURL: configuration.rideSystems.baseURL,
                apiKey: configuration.rideSystems.apiKey,
        )

        environment.healthChecks().register("RideSystems",
                new RideSystemsHealthCheck(rideSystemsDAO))

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
