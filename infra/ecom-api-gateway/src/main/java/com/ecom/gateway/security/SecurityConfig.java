package com.ecom.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .exceptionHandling(errors -> errors
                        .authenticationEntryPoint((exchange, exception) -> writeError(
                                exchange, HttpStatus.UNAUTHORIZED, "Authentication required"))
                        .accessDeniedHandler((exchange, exception) -> writeError(
                                exchange, HttpStatus.FORBIDDEN, "Insufficient role")))

                .authorizeExchange(exchange -> exchange
                        .pathMatchers(
                                "/auth/**",
                                "/actuator/health")
                        .permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/product", "/api/v1/product/**")
                        .hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/product", "/api/v1/product/**")
                        .hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/product/**")
                        .hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/product/**")
                        .hasRole("ADMIN")
                        .pathMatchers("/api/v1/order/**")
                        .hasAnyRole("USER", "ADMIN")
                        .pathMatchers(HttpMethod.GET, "/api/v1/inventory/**")
                        .hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/v1/inventory/**")
                        .hasRole("ADMIN")
                        .pathMatchers("/api/v1/payment/**")
                        .hasAnyRole("USER", "ADMIN")
                        .anyExchange().authenticated())

                .addFilterAt(
                        jwtAuthenticationFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION)

                .build();
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        byte[] body = ("{\"error\":\"" + message + "\"}").getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                .bufferFactory().wrap(body)));
    }
}
