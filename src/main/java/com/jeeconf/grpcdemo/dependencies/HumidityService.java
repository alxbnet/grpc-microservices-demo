package com.jeeconf.grpcdemo.dependencies;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.Humidity;
import com.jeeconf.grpcdemo.dependencies.HumidityServiceGrpc.HumidityServiceImplBase;
import com.jeeconf.grpcdemo.providers.RandomHumidityProvider;
import io.grpc.stub.StreamObserver;

import java.util.function.Supplier;

/**
 * Replies with randomly generated {@link Humidity}.
 */
public class HumidityService extends HumidityServiceImplBase {

    private final Supplier<Humidity> humidityProvider = new RandomHumidityProvider();

    @Override
    public void getCurrent(Coordinates request, StreamObserver<Humidity> responseObserver) {
        responseObserver.onNext(humidityProvider.get());
        responseObserver.onCompleted();
    }

}
