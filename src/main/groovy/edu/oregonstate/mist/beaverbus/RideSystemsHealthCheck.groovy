package edu.oregonstate.mist.beaverbus

import com.codahale.metrics.health.HealthCheck
import com.codahale.metrics.health.HealthCheck.Result

class RideSystemsHealthCheck extends HealthCheck {
    RideSystemsDAO rideSystemsDAO

    RideSystemsHealthCheck(RideSystemsDAO rideSystemsDAO) {
        this.rideSystemsDAO = rideSystemsDAO
    }

    @Override
    protected Result check() throws Exception {
        // make any api call
        rideSystemsDAO.getMapVehiclePoints(null)

        Result.healthy()
    }
}
