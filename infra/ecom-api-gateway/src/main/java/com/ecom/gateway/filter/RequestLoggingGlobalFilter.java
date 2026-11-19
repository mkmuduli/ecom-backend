package com.ecom.gateway.filter;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class RequestLoggingGlobalFilter implements GlobalFilter, Ordered {
        private static final String HEADER = "X-Correlation-ID";

        @Override
        public Mono<Void> filter(
                        ServerWebExchange exchange,
                        GatewayFilterChain chain) {

                long start = System.currentTimeMillis();

                String method = exchange.getRequest()
                                .getMethod()
                                .name();

                String path = exchange.getRequest()
                                .getURI()
                                .getPath();

                // 1. Check whether client already sent ID
                String incomingCorrelationId = exchange.getRequest()
                                .getHeaders()
                                .getFirst(HEADER);

                // 2. Generate ID if missing
                final String correlationId = incomingCorrelationId == null ||
                                incomingCorrelationId.isBlank()
                                                ? UUID.randomUUID().toString()
                                                : incomingCorrelationId;

                // 3. Create modified request
                ServerHttpRequest request = exchange.getRequest()
                                .mutate()
                                .header(HEADER, correlationId)
                                .build();

                // 4. Create modified exchange
                ServerWebExchange modifiedExchange = exchange.mutate()
                                .request(request)
                                .build();

                log.info("Gateway 1 request method={} path={} correlationId={}",
                                method,
                                path,
                                correlationId);

                exchange.getResponse()
                                .getHeaders()
                                .add(HEADER, correlationId);

                // 5. Continue the request
                return chain.filter(modifiedExchange).doFinally(signal -> {

                        long duration = System.currentTimeMillis() - start;

                        log.info(
                                        "Gateway 2 response status={} path={} correlationId={} durationMs={}",
                                        exchange.getResponse().getStatusCode(),
                                        path,
                                        correlationId,
                                        duration);

                });
        }

        @Override
        public int getOrder() {
                return -90;
        }
}