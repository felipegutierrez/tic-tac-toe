package com.adsquare.tictactoe.service;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {

    private final Random random;
    private final List<Integer> availablePositions;
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
        this.availablePositions = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        this.random = new Random();
    }

    public Mono<Board> startNewBoard() {
        var board = new Board(null, getRandomPlayer().name(), "", false, List.of());
        return boardRepository.save(board);
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
        return findBoard(move.getBoardId())
            .flatMap(board -> {
                if (board.getPlayerOnTurn().equals(move.getPlayer())) {
                    if (isPositionAvailable(board, move)) {
                        board.setPlayerOnTurn(move.getPlayer());
                        board.getScores().add(new Score(null, move.getPlayer(), move.getPosition(), LocalTime.now()));
                        return boardRepository.save(board);
                    } else {
                        return Mono.error(new Exception("Position " + move.getPosition() + " is not available"));
                    }
                } else {
                    return Mono.error(new Exception("It is not the turn of player " + move.getPlayer()));
                }
            })
            .log();
    }

    /**
     * Terminate all boards by setting "complete = TRUE" regardless if the game was finished
     */
    public Flux<Board> terminateAllBoards() {
        return boardRepository.findAll()
            .filter(b -> b.getBoardComplete().equals(Boolean.FALSE))
            .flatMap(b -> {
                b.setBoardComplete(Boolean.TRUE);
                return boardRepository.save(b);
            })
            .log();
    }

    /**
     * Verifies if a given position is available in the giver board
     *
     * @param move
     * @return
     */
    protected boolean isPositionAvailable(final Board board, final Move move) {
        if (!availablePositions.contains(move.getPosition()) ||
            Boolean.TRUE.equals(board.getBoardComplete()) ||
            !board.getPlayerOnTurn().equals(move.getPlayer())) {
            return false;
        }
        return board.getScores()
            .stream()
            .filter(score -> score.getPosition().equals(move.getPosition()))
            .findFirst()
            .isEmpty();
    }

    private Player getRandomPlayer() {
        int randomBinary = random.nextInt(1000) % 2;
        return (randomBinary == 0 ? Player.A : Player.B);
    }
}
