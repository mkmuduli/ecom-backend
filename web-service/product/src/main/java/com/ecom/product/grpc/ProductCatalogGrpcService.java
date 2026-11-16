package com.ecom.product.grpc;

import com.ecom.grpc.product.v1.GetProductPriceRequest;
import com.ecom.grpc.product.v1.GetProductPriceResponse;
import com.ecom.grpc.product.v1.ProductCatalogGrpc;
import com.ecom.product.exception.ProductNotFoundException;
import com.ecom.product.repository.ProductRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ProductCatalogGrpcService extends ProductCatalogGrpc.ProductCatalogImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ProductCatalogGrpcService.class);

    private final ProductRepository productRepository;

    public ProductCatalogGrpcService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void getProductPrice(GetProductPriceRequest request,
            StreamObserver<GetProductPriceResponse> responseObserver) {
        try {
            var product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

            responseObserver.onNext(GetProductPriceResponse.newBuilder()
                    .setPrice(product.getPrice().toPlainString())
                    .build());
            responseObserver.onCompleted();
        } catch (ProductNotFoundException exception) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Product not found: " + request.getProductId())
                    .asRuntimeException());
        } catch (RuntimeException exception) {
            logger.error("Failed to retrieve price for product {}", request.getProductId(), exception);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Unable to retrieve product price")
                    .withCause(exception)
                    .asRuntimeException());
        }
    }
}
