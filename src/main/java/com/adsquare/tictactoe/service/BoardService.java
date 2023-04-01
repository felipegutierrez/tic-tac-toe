package com.adsquare.tictactoe.service;

import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.adsquare.tictactoe.domain.Board;
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

    public static void main(String[] args) {
        BoardService boardService = new BoardService(null);
        boardService.getRandomPlayer();
    }

    public Mono<Board> startNewBoard() {
        var board = new Board(null, getRandomPlayer().name(), "", false, List.of());
        return boardRepository.save(board);
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

    public Flux<Board> getAllBoards() {
        return boardRepository.findAll();
    }
}
