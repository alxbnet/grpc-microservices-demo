package com.jeeconf.grpcdemo.dependencies;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.Wind;
import com.jeeconf.grpcdemo.dependencies.WindServiceGrpc.WindServiceImplBase;
import com.jeeconf.grpcdemo.providers.RandomWindProvider;
import io.grpc.stub.StreamObserver;

import java.util.function.Supplier;

/**
 * Replies with randomly generated {@link Wind}.
 */
public class WindService extends WindServiceImplBase {

    private final Supplier<Wind> windProvider = new RandomWindProvider();

    @Override
    public void getCurrent(Coordinates request, StreamObserver<Wind> responseObserver) {
        responseObserver.onNext(windProvider.get());
        responseObserver.onCompleted();
    }
}