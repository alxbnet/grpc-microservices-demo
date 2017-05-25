package com.jeeconf.grpcdemo.streaming;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.Temperature;
import com.jeeconf.grpcdemo.providers.RandomTemperatureProvider;
import com.jeeconf.grpcdemo.streaming.TemperatureStreamingServiceGrpc.TemperatureStreamingServiceImplBase;
import io.grpc.stub.StreamObserver;

/**
 * Periodically streams random {@link Temperature} values.
 */
public class TemperatureStreamingService extends TemperatureStreamingServiceImplBase {

    private final RandomValuesStreamer<Temperature> valuesStreamer =
            new RandomValuesStreamer<>(new RandomTemperatureProvider());

    @Override
    public StreamObserver<Coordinates> observe(StreamObserver<Temperature> responseObserver) {
        return valuesStreamer.stream(responseObserver);
    }
}
