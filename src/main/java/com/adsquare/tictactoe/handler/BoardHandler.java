package com.adsquare.tictactoe.handler;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.exception.BoardException;
import com.adsquare.tictactoe.exception.BoardNotFoundException;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.TicTacToeRules;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class BoardHandler {

    private final BoardRepository boardRepository;
    private final TicTacToeRules ticTacToeRules;
    private final Validator validator;

    public BoardHandler(
        final BoardRepository boardRepository,
        final TicTacToeRules ticTacToeRules,
        final Validator validator
    ) {
        this.boardRepository = boardRepository;
        this.ticTacToeRules = ticTacToeRules;
        this.validator = validator;
    }

    public Mono<ServerResponse> startNewBoard() {
        return boardRepository
            .save(new Board(null, ticTacToeRules.getRandomPlayer(), null, false, List.of()))
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
            .log();
    }

    public Mono<ServerResponse> getAllBoards() {
        var boardFlux = boardRepository.findAll();
        return ServerResponse.ok().body(boardFlux, Board.class).log();
    }

    public Mono<ServerResponse> getBoard(final ServerRequest request) {
        var boardId = request.pathVariable("id");
        var boardMono = boardRepository
            .findById(boardId)
            .switchIfEmpty(Mono.error(new BoardNotFoundException("Board " + boardId + " not found")));
        return ServerResponse.ok().body(boardMono, Board.class).log();
    }

    public Mono<ServerResponse> playBoard(final ServerRequest request) {
        var moveMono = request.bodyToMono(Move.class);
        final Mono<Board> boardMonoSaved = moveMono
            .doOnNext(this::validate)
            .flatMap(move -> boardRepository.findById(move.boardId())
                .flatMap(board -> {
                    if (!board.getPlayerOnTurn().equals(move.player())) {
                        return Mono.error(new BoardException("It is not the turn of player " + move.player()));
                    }
                    if (!ticTacToeRules.isPositionAvailable(board, move)) {
                        return Mono.error(new BoardException("Position " + move.position() + " is not available"));
                    }
                    board.getScores().add(new Score(move.player(), move.position()));
                    if (ticTacToeRules.isBoardComplete(board)) {
                        board.setPlayerOnTurn(null);
                        board.setBoardComplete(true);
                        board.setWinnerPlayer(ticTacToeRules.getWinnerPlayer(board));
                    } else {
                        board.setPlayerOnTurn(ticTacToeRules.getNextPlayer(board));
                    }
                    return boardRepository.save(board);
                }))
            .switchIfEmpty(Mono.error(new BoardException("Board not found")));
        return ServerResponse.ok().body(boardMonoSaved, Board.class).log();
    }

    private void validate(final Move move) {
        var constraintViolations = validator.validate(move);
        log.info("ConstraintViolations: {}", constraintViolations);
        if (!constraintViolations.isEmpty()) {
            var errorMessage = constraintViolations
                .stream()
                .map(ConstraintViolation::getMessage)
                .sorted()
                .collect(Collectors.joining(","));
            throw new BoardException(errorMessage);
        }
    }

    public Mono<ServerResponse> deleteAllBoards() {
        final Mono<Void> voidMono = boardRepository.deleteAll();
        return ServerResponse.ok().body(voidMono, Board.class).log();
    }

    public Mono<ServerResponse> deleteBoard(final ServerRequest request) {
        return boardRepository
            .findById(request.pathVariable("id"))
            .flatMap(board -> boardRepository.deleteById(board.getId()))
            .then(ServerResponse.noContent().build())
            .log();
    }
}
