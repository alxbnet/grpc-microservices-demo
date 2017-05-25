package com.jeeconf.grpcdemo;

import com.jeeconf.grpcdemo.WeatherServiceGrpc.WeatherServiceImplBase;
import io.grpc.stub.StreamObserver;

import static com.jeeconf.grpcdemo.Temperature.Units.CELSUIS;

/**
 * Returns hard-coded weather response.
 */
public class WeatherService extends WeatherServiceImplBase {

    @Override
    public void getCurrent(WeatherRequest request, StreamObserver<WeatherResponse> responseObserver) {
        WeatherResponse response = WeatherResponse.newBuilder()
                .setTemperature(Temperature.newBuilder().setUnits(CELSUIS).setDegrees(20.f))
                .setHumidity(Humidity.newBuilder().setValue(.65f))
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
