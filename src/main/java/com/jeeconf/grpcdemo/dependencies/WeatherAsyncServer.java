package com.jeeconf.grpcdemo.dependencies;

import com.jeeconf.grpcdemo.dependencies.HumidityServiceGrpc.HumidityServiceFutureStub;
import com.jeeconf.grpcdemo.dependencies.TemperatureServiceGrpc.TemperatureServiceFutureStub;
import com.jeeconf.grpcdemo.dependencies.WindServiceGrpc.WindServiceFutureStub;
import io.grpc.Server;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;

/**
 * Starts weather, temperature, humidity and wind servers.
 */
public class WeatherAsyncServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        String host = "localhost";
        int temperaturePort = 8081;
        int humidityPort = 8082;
        int windPort = 8083;

        Server temperatureServer = NettyServerBuilder.forPort(temperaturePort)
                .addService(new TemperatureService()).build().start();
        Server humidityServer = NettyServerBuilder.forPort(humidityPort)
                .addService(new HumidityService()).build().start();
        Server windServer = NettyServerBuilder.forPort(windPort).addService(new WindService()).build().start();

        TemperatureServiceFutureStub temperatureStub =
                TemperatureServiceGrpc.newFutureStub(NettyChannelBuilder.forAddress(host, temperaturePort).usePlaintext(true).build());
        HumidityServiceFutureStub humidityStub =
                HumidityServiceGrpc.newFutureStub(NettyChannelBuilder.forAddress(host, humidityPort).usePlaintext(true).build());
        WindServiceFutureStub windStub =
                WindServiceGrpc.newFutureStub(NettyChannelBuilder.forAddress(host, windPort).usePlaintext(true).build());

        WeatherAsyncService weatherService = new WeatherAsyncService(temperatureStub, humidityStub, windStub);
        Server weatherServer = NettyServerBuilder.forPort(8090).addService(weatherService).build().start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            weatherServer.shutdownNow();
            temperatureServer.shutdownNow();
            humidityServer.shutdownNow();
            windServer.shutdownNow();
        }));

        weatherServer.awaitTermination();
    }
}
