package com.ecom.order.grpc;

import com.ecom.grpc.product.v1.GetProductPriceRequest;
import com.ecom.grpc.product.v1.ProductCatalogGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Component
public class ProductPriceClient {

    private static final long REQUEST_DEADLINE_SECONDS = 2;
    private static final long RETRY_DELAY_MILLIS = 5_000;

    private final ManagedChannel channel;
    private final ProductCatalogGrpc.ProductCatalogBlockingStub stub;

    public ProductPriceClient(
            @Value("${product.grpc.host:localhost}") String host,
            @Value("${product.grpc.port:9091}") int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.stub = ProductCatalogGrpc.newBlockingStub(channel);
    }

    public BigDecimal getPrice(Long productId) {
        try {
            return requestPrice(productId);
        } catch (StatusRuntimeException exception) {
            if (!isRetryable(exception)) {
                throw exception;
            }

            waitBeforeRetry();
            return requestPrice(productId);
        }
    }

    private BigDecimal requestPrice(Long productId) {
        var response = stub.withDeadlineAfter(REQUEST_DEADLINE_SECONDS, TimeUnit.SECONDS)
                .getProductPrice(GetProductPriceRequest.newBuilder()
                        .setProductId(productId)
                        .build());
        return new BigDecimal(response.getPrice());
    }

    private boolean isRetryable(StatusRuntimeException exception) {
        Status.Code code = exception.getStatus().getCode();
        return code == Status.Code.UNAVAILABLE
                || code == Status.Code.DEADLINE_EXCEEDED
                || code == Status.Code.RESOURCE_EXHAUSTED;
    }

    private void waitBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MILLIS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw Status.CANCELLED
                    .withDescription("Interrupted while waiting to retry product price lookup")
                    .withCause(exception)
                    .asRuntimeException();
        }
    }

    @PreDestroy
    public void shutdown() {
        channel.shutdown();
    }
}
