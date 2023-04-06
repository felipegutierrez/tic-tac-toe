package com.adsquare.tictactoe.service;

import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.exception.BoardException;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.TicTacToeRules;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final TicTacToeRules ticTacToeRules;

    public BoardService(final BoardRepository boardRepository, final TicTacToeRules ticTacToeRules) {
        this.boardRepository = boardRepository;
        this.ticTacToeRules = ticTacToeRules;
    }

    public Mono<Board> startNewBoard() {
        return boardRepository.save(
            new Board(null, ticTacToeRules.getRandomPlayer().name(), Strings.EMPTY, false, List.of())
        );
    }

    public Mono<Void> deleteAllBoards() {
        return boardRepository.deleteAll();
    }

    public Flux<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Mono<Board> findBoard(final String boardId) {
        return boardRepository.findById(boardId);
    }

    public Mono<Board> updateBoard(Move move) {
        return findBoard(move.boardId())
            .switchIfEmpty(Mono.error(new BoardException("Board " + move.boardId() + " not found")))
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
            })
            .log();
    }

}
