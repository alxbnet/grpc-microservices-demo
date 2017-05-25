package com.jeeconf.grpcdemo;

import io.grpc.Server;
import io.grpc.netty.NettyServerBuilder;

import java.io.IOException;

/**
 * Starts gRPC server with {@link WeatherService}.
 */
public class WeatherServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server grpcServer = NettyServerBuilder.forPort(8090)
                .addService(new WeatherService()).build()
                .start();

        Runtime.getRuntime().addShutdownHook(new Thread(grpcServer::shutdown));
        grpcServer.awaitTermination();
    }
}
