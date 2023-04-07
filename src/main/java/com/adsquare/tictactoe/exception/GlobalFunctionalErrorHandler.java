package com.adsquare.tictactoe.exception;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GlobalFunctionalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(final ServerWebExchange exchange, final Throwable ex) {
        log.error("Exception message: {}", ex.getMessage(), ex);
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        final DataBuffer errorMessage = dataBufferFactory.wrap(ex.getMessage().getBytes());

        if (ex instanceof BoardException) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        } else if (ex instanceof BoardNotFoundException) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return exchange.getResponse().writeWith(Mono.just(errorMessage));
    }
}
