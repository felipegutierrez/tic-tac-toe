package com.adsquare.tictactoe.router;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.adsquare.tictactoe.handler.BoardHandler;

@Configuration
public class BoardRouter {

    @Bean
    public RouterFunction<ServerResponse> boardRoute(BoardHandler boardHandler) {
        return route()
            .POST("/v2/startboard", boardHandler::startNewBoard)
            .build();
    }
}
