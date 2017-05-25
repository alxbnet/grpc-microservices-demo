package com.jeeconf.grpcdemo.streaming;

import io.grpc.stub.StreamObserver;

import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Periodically (with a pseudo-random rate) streams values returned by {@link Supplier<ResponseType>#get()}
 * to a provided instance if {@link StreamObserver<ResponseType>}.
 */
class RandomValuesStreamer<ResponseType> {

    private final Set<StreamObserver<ResponseType>> observers = ConcurrentHashMap.newKeySet();
    private final Supplier<ResponseType> valueProvider;

    RandomValuesStreamer(Supplier<ResponseType> valueProvider) {
        this.valueProvider = valueProvider;

        long randomDelayInMillis = TimeUnit.SECONDS.toMillis(1 + new Random().nextInt(4));
        Timer timer = new Timer();
        timer.schedule(new RandomValuesStreamer.GenerateValueTask(), randomDelayInMillis, randomDelayInMillis);
    }

    <RequestType> StreamObserver<RequestType> stream(StreamObserver<ResponseType> responseObserver) {

        observers.add(responseObserver);

        return new StreamObserver<RequestType>() {
            @Override
            public void onNext(RequestType request) {
                responseObserver.onNext(valueProvider.get());
            }

            @Override
            public void onError(Throwable t) {
                boolean removed = observers.remove(responseObserver);
                if (removed) {
                    responseObserver.onError(t);
                }
            }

            @Override
            public void onCompleted() {
                boolean removed = observers.remove(responseObserver);
                if (removed) {
                    responseObserver.onCompleted();
                }
            }
        };
    }

    private class GenerateValueTask extends TimerTask {
        @Override
        public void run() {
            ResponseType value = valueProvider.get();
            observers.forEach(o -> o.onNext(value));
        }
    }

}
