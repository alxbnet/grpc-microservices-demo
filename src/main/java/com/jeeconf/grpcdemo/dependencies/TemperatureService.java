package com.jeeconf.grpcdemo.dependencies;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.Temperature;
import com.jeeconf.grpcdemo.dependencies.TemperatureServiceGrpc.TemperatureServiceImplBase;
import com.jeeconf.grpcdemo.providers.RandomTemperatureProvider;
import io.grpc.stub.StreamObserver;

import java.util.function.Supplier;

/**
 * Replies with randomly generated {@link Temperature}.
 */
public class TemperatureService extends TemperatureServiceImplBase {

    private final Supplier<Temperature> temperatureProvider = new RandomTemperatureProvider();

    @Override
    public void getCurrent(Coordinates request, StreamObserver<Temperature> responseObserver) {
        responseObserver.onNext(temperatureProvider.get());
        responseObserver.onCompleted();
    }

}
