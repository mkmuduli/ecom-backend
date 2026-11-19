package com.ecom.gateway.security;

import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;

@Configuration(proxyBeanMethods = false)
public class RateLimitKeyResolvers {

    @Bean
    @Primary
    KeyResolver userKeyResolver() {
        return exchange -> ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .filter(authentication -> authentication.isAuthenticated())
                .map(authentication -> "user:" + authentication.getName())
                .switchIfEmpty(exchange.getPrincipal()
                        .map(principal -> "user:" + principal.getName()))
                .switchIfEmpty(reactor.core.publisher.Mono.fromSupplier(() ->
                        "ip:" + Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                                .map(address -> address.getAddress() != null
                                        ? address.getAddress().getHostAddress()
                                        : address.getHostString())
                                .orElse("unknown")));
    }

    @Bean
    KeyResolver clientIpKeyResolver() {
        return exchange -> reactor.core.publisher.Mono.fromSupplier(() ->
                Optional.ofNullable(exchange.getRequest().getRemoteAddress())
                        .map(address -> address.getAddress() != null
                                ? address.getAddress().getHostAddress()
                                : address.getHostString())
                        .orElse("unknown"));
    }
}
