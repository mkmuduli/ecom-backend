package com.ecom.product.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ProductGrpcServer implements SmartLifecycle {

    private final int port;
    private final ProductCatalogGrpcService productCatalogGrpcService;
    private Server server;

    public ProductGrpcServer(
            @Value("${grpc.server.port:9091}") int port,
            ProductCatalogGrpcService productCatalogGrpcService) {
        this.port = port;
        this.productCatalogGrpcService = productCatalogGrpcService;
    }

    @Override
    public synchronized void start() {
        if (server != null && !server.isShutdown()) {
            return;
        }
        try {
            server = ServerBuilder.forPort(port)
                    .addService(productCatalogGrpcService)
                    .build()
                    .start();
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to start product gRPC server on port " + port, exception);
        }
    }

    @Override
    public synchronized void stop() {
        if (server != null) {
            server.shutdown();
            server = null;
        }
    }

    @Override
    public boolean isRunning() {
        return server != null && !server.isShutdown();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
