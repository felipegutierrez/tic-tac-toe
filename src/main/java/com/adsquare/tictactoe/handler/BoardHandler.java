package com.adsquare.tictactoe.handler;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.exception.BoardException;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.TicTacToeRules;
import reactor.core.publisher.Mono;

@Component
public class BoardHandler {

    private final BoardRepository boardRepository;
    private final TicTacToeRules ticTacToeRules;

    public BoardHandler(final BoardRepository boardRepository, final TicTacToeRules ticTacToeRules) {
        this.boardRepository = boardRepository;
        this.ticTacToeRules = ticTacToeRules;
    }

    public Mono<ServerResponse> startNewBoard(final ServerRequest request) {
        return request.bodyToMono(String.class)
            .flatMap(boardId -> boardRepository.save(
                new Board(
                    (boardId == null || Strings.isBlank(boardId)) ? null : boardId,
                    ticTacToeRules.getRandomPlayer().name(),
                    Strings.EMPTY,
                    false,
                    List.of()
                )
            ))
            .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue)
            .log();

    }

    public Mono<ServerResponse> getAllBoards(final ServerRequest request) {
        var boardFlux = boardRepository.findAll();
        return ServerResponse.ok().body(boardFlux, Board.class).log();
    }

    public Mono<ServerResponse> getBoard(final ServerRequest request) {
        var boardId = request.pathVariable("id");
        if (Strings.isEmpty(boardId)) {
            throw new BoardException("BoardId is empty.");
        }
        var boardMono = boardRepository.findById(request.pathVariable("id"));
        return ServerResponse.ok().body(boardMono, Board.class).log();
    }

    public Mono<ServerResponse> playBoard(final ServerRequest request) {
        var moveMono = request.bodyToMono(Move.class);
        final Mono<Board> boardMonoSaved = moveMono.flatMap(move -> boardRepository.findById(move.boardId())
                .flatMap(board -> {
                    if (!board.getPlayerOnTurn().equals(move.player())) {
                        return Mono.error(new BoardException("It is not the turn of player " + move.player()));
                    }
                    if (!ticTacToeRules.isPositionAvailable(board, move)) {
                        return Mono.error(new BoardException("Position " + move.position() + " is not available"));
                    }
                    board.getScores().add(new Score(move.player(), move.position()));
                    if (ticTacToeRules.isBoardComplete(board)) {
                        board.setPlayerOnTurn(Strings.EMPTY);
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

    public Mono<ServerResponse> deleteAllBoards(final ServerRequest serverRequest) {
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
