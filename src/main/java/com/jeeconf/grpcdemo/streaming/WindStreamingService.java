package com.jeeconf.grpcdemo.streaming;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.Wind;
import com.jeeconf.grpcdemo.providers.RandomWindProvider;
import com.jeeconf.grpcdemo.streaming.WindStreamingServiceGrpc.WindStreamingServiceImplBase;
import io.grpc.stub.StreamObserver;

/**
 * Periodically streams random {@link Wind} values.
 */
public class WindStreamingService extends WindStreamingServiceImplBase {

    private final RandomValuesStreamer<Wind> valuesStreamer = new RandomValuesStreamer<>(new RandomWindProvider());

    @Override
    public StreamObserver<Coordinates> observe(StreamObserver<Wind> responseObserver) {
        return valuesStreamer.stream(responseObserver);
    }

}
