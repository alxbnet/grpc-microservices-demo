package com.jeeconf.grpcdemo;

import com.jeeconf.grpcdemo.WeatherServiceGrpc.WeatherServiceBlockingStub;
import com.jeeconf.grpcdemo.WeatherServiceGrpc.WeatherServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Semaphore;

/**
 * Subscribes to weather service with blocking and async stubs and prints its responses to the "standard" output stream.
 */
public class WeatherClient {

    public static void main(String[] args) throws InterruptedException {

        ManagedChannel grpcChannel = NettyChannelBuilder.forAddress("localhost", 8090).usePlaintext(true).build();

        WeatherServiceBlockingStub blockingClient = WeatherServiceGrpc.newBlockingStub(grpcChannel);
        WeatherServiceStub asyncClient = WeatherServiceGrpc.newStub(grpcChannel);

        WeatherRequest request = WeatherRequest.newBuilder()
                .setCoordinates(Coordinates.newBuilder().setLatitude(KyivCoordinates.LATITUDE)
                        .setLongitude(KyivCoordinates.LONGITUDE)).build();

        WeatherResponse response = blockingClient.getCurrent(request);
        System.out.printf("Blocking client. Current weather for %s: %s.%n", request, response);

        Semaphore exitSemaphore = new Semaphore(0);
        asyncClient.getCurrent(request, new StreamObserver<WeatherResponse>() {

            @Override
            public void onNext(WeatherResponse response) {
                System.out.printf("Async client. Current weather for %s: %s.%n", request, response);
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                System.err.printf("Async client. Cannot get weather for %s.%n", request);
                exitSemaphore.release();
            }

            @Override
            public void onCompleted() {
                System.out.printf("Async client. Stream completed.%n");
                exitSemaphore.release();
            }
        });

        exitSemaphore.acquire();

    }
}
