package com.jeeconf.grpcdemo.streaming;

import com.google.common.collect.ImmutableList;
import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.WeatherRequest;
import com.jeeconf.grpcdemo.WeatherResponse;
import com.jeeconf.grpcdemo.WeatherResponse.Builder;
import com.jeeconf.grpcdemo.streaming.HumidityStreamingServiceGrpc.HumidityStreamingServiceStub;
import com.jeeconf.grpcdemo.streaming.TemperatureStreamingServiceGrpc.TemperatureStreamingServiceStub;
import com.jeeconf.grpcdemo.streaming.WeatherStreamingServiceGrpc.WeatherStreamingServiceImplBase;
import com.jeeconf.grpcdemo.streaming.WindStreamingServiceGrpc.WindStreamingServiceStub;
import io.grpc.stub.StreamObserver;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;

/**
 * Subscribes to temperature, humidity and wind services and streams their response back to a client.
 */
public class WeatherStreamingService extends WeatherStreamingServiceImplBase {

    private final TemperatureStreamingServiceStub temperatureService;
    private final HumidityStreamingServiceStub humidityService;
    private final WindStreamingServiceStub windService;

     WeatherStreamingService(TemperatureStreamingServiceStub temperatureService,
                                   HumidityStreamingServiceStub humidityService,
                                   WindStreamingServiceStub windService) {
        this.temperatureService = temperatureService;
        this.humidityService = humidityService;
        this.windService = windService;
    }

    @Override
    public StreamObserver<WeatherRequest> observe(StreamObserver<WeatherResponse> responseObserver) {

        AutoClosableLock lock = new AutoClosableLock(new ReentrantLock());
        StreamObserver<Coordinates> temperatureClientStream =
                temperatureService.observe(newStreamObserver(responseObserver, lock,
                        Builder::setTemperature));

        StreamObserver<Coordinates> humidityClientStream =
                humidityService.observe(newStreamObserver(responseObserver, lock,
                        Builder::setHumidity));

        StreamObserver<Coordinates> windClientStream =
                windService.observe(newStreamObserver(responseObserver, lock,
                        Builder::setWind));

        List<StreamObserver<Coordinates>> clientStreams =
                ImmutableList.copyOf(asList(temperatureClientStream, humidityClientStream, windClientStream));

        return new StreamObserver<WeatherRequest>() {

            @Override
            public void onNext(WeatherRequest request) {
                clientStreams.forEach(s -> s.onNext(request.getCoordinates()));
            }

            @Override
            public void onError(Throwable e) {
                clientStreams.forEach(s -> s.onError(e));
            }

            @Override
            public void onCompleted() {
                clientStreams.forEach(StreamObserver::onCompleted);
            }
        };
    }

    private static <T> StreamObserver<T> newStreamObserver(
            StreamObserver<WeatherResponse> responseObserver,
            AutoClosableLock observerLock,
            BiFunction<Builder, T, Builder> convert) {
        return new StreamObserver<T>() {
            @Override
            public void onNext(T value) {
                WeatherResponse response = convert.apply(WeatherResponse.newBuilder(), value).build();
                try(AutoClosableLock ignored = observerLock.lock()) {
                    responseObserver.onNext(response);
                }
            }

            @Override
            public void onError(Throwable e) {
                try(AutoClosableLock ignored = observerLock.lock()) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onCompleted() {
                try(AutoClosableLock ignored = observerLock.lock()) {
                    responseObserver.onCompleted();
                }
            }
        };
    }

    private static final class AutoClosableLock implements AutoCloseable {
        private final Lock lock;

        private AutoClosableLock(Lock lock) {
            this.lock = lock;
        }

        AutoClosableLock lock() {
            lock.lock();
            return this;
        }

        @Override
        public void close() {
            lock.unlock();
        }
    }
}
