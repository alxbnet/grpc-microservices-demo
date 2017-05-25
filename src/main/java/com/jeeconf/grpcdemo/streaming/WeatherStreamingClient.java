package com.jeeconf.grpcdemo.streaming;

import com.jeeconf.grpcdemo.Coordinates;
import com.jeeconf.grpcdemo.KyivCoordinates;
import com.jeeconf.grpcdemo.WeatherRequest;
import com.jeeconf.grpcdemo.WeatherResponse;
import com.jeeconf.grpcdemo.streaming.WeatherStreamingServiceGrpc.WeatherStreamingServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Semaphore;

/**
 * Subscribes to weather service and prints its responses to the "standard" output stream.
 */
public class WeatherStreamingClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 8090).usePlaintext(true).build();

        WeatherStreamingServiceStub stub = WeatherStreamingServiceGrpc.newStub(channel);

        Semaphore exitSemaphore = new Semaphore(0);
        StreamObserver<WeatherRequest> requestStream = stub.observe(new StreamObserver<WeatherResponse>() {
            @Override
            public void onNext(WeatherResponse response) {
                System.out.printf("Async client onNext: %s%n", response);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                exitSemaphore.release();
            }

            @Override
            public void onCompleted() {
                System.out.println("Call completed!");
                exitSemaphore.release();
            }
        });

        WeatherRequest request = WeatherRequest.newBuilder()
                .setCoordinates(Coordinates.newBuilder()
                        .setLatitude(KyivCoordinates.LATITUDE).setLongitude(KyivCoordinates.LONGITUDE))
                .build();
        requestStream.onNext(request);

        exitSemaphore.acquire();
    }

}
