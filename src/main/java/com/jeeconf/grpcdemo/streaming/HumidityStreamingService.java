package com.jeeconf.grpcdemo.streaming;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.Humidity;
import com.jeeconf.grpcdemo.providers.RandomHumidityProvider;
import com.jeeconf.grpcdemo.streaming.HumidityStreamingServiceGrpc.HumidityStreamingServiceImplBase;
import io.grpc.stub.StreamObserver;

/**
 * Periodically streams random {@link Humidity} values.
 */
public class HumidityStreamingService extends HumidityStreamingServiceImplBase {

    private final RandomValuesStreamer<Humidity> valuesStreamer =
            new RandomValuesStreamer<>(new RandomHumidityProvider());

    @Override
    public StreamObserver<Coordinates> observe(StreamObserver<Humidity> responseObserver) {
        return valuesStreamer.stream(responseObserver);
    }
}
