package com.adsquare.tictactoe.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
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
            .nest(path("/v2"), builder ->
                builder
                    .GET("/showboards", boardHandler::getAllBoards)
                    .GET("/showboard/{id}", boardHandler::getBoard)
                    .POST("/startboard", boardHandler::startNewBoard)
                    .PUT("/playboard", boardHandler::playBoard)
                    .DELETE("/deleteboards", boardHandler::deleteAllBoards)
                    .DELETE("/deleteboard/{id}", boardHandler::deleteBoard)
            )
            .build();
    }
}
