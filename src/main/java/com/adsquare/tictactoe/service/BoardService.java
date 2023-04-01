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

    private final BoardRepository boardRepository;
    private final Random random;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
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
                board.setPlayerOnTurn(
                    board.getPlayerOnTurn().equals(Player.A.name()) ? Player.B.name() : Player.A.name()
                );
                board.getScores().add(new Score(null, move.getPlayer(), move.getPosition(), LocalTime.now()));
                return boardRepository.save(board);
            })
            .log();
    }

    /**
     * Terminate all boards by setting "complete = TRUE" regardless if the game was finished
     */
    public void terminateAllBoards() {
        System.out.println("----------");
        final Flux<Board> boards =
            boardRepository.findAll()
                .filter(b -> b.getBoardComplete().equals(Boolean.FALSE))
                .map(b -> {
                    b.setBoardComplete(Boolean.TRUE);
                    return b;
                })
                .log();
        System.out.println("----------");
        // final Flux<Board> result =
        boardRepository.saveAll(boards).log();
        System.out.println("----------");
        // TODO: terminate all boards by setting complete to TRUE
    }

    private Player getRandomPlayer() {
        int randomBinary = random.nextInt(1000) % 2;
        return (randomBinary == 0 ? Player.A : Player.B);
    }
}
