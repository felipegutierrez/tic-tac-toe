package com.adsquare.tictactoe.handler;

import static com.adsquare.tictactoe.util.TicTacToeRules.getRandomPlayer;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.repository.BoardRepository;
import reactor.core.publisher.Mono;

@Component
public class BoardHandler {

    private final BoardRepository boardRepository;

    public BoardHandler(final BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Mono<ServerResponse> startNewBoard(final ServerRequest request) {
        return request.bodyToMono(String.class)
            .flatMap(boardId -> boardRepository.save(
                new Board(
                    (boardId == null || Strings.isBlank(boardId)) ? null : boardId,
                    getRandomPlayer().name(),
                    Strings.EMPTY,
                    false,
                    List.of()
                )
            ))
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
            .log();

    }
}
