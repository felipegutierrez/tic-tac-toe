package com.adsquare.tictactoe.service;

import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.util.Strings;
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
    private final List<List<Integer>> completedPositions;
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
        this.availablePositions = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        this.completedPositions = List.of(
            // horizontal
            List.of(1, 2, 3),
            List.of(4, 5, 6),
            List.of(7, 8, 9),
            // vertical
            List.of(1, 4, 7),
            List.of(2, 5, 8),
            List.of(3, 6, 9),
            // diagonal
            List.of(1, 5, 9),
            List.of(3, 5, 7)
        );
        this.random = new Random();
    }

    public Mono<Board> startNewBoard() {
        var board = new Board(null, getRandomPlayer().name(), Strings.EMPTY, false, List.of());
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
        return findBoard(move.boardId())
            .flatMap(board -> {
                if (!board.getPlayerOnTurn().equals(move.player())) {
                    return Mono.error(new Exception("It is not the turn of player " + move.player()));
                }
                if (!isPositionAvailable(board, move)) {
                    return Mono.error(new Exception("Position " + move.position() + " is not available"));
                }
                board.setPlayerOnTurn(getNextPlayer(board));
                board.getScores().add(new Score(move.player(), move.position()));
                if (isBoardComplete(board)) {
                    board.setBoardComplete(true);
                    board.setWinnerPlayer(getWinnerPlayer(board));
                }
                return boardRepository.save(board);
            })
            .log();
    }

    /**
     * Verifies if a given position is available in the giver board
     *
     * @param board
     * @param move
     * @return
     */
    protected boolean isPositionAvailable(final Board board, final Move move) {
        if (!availablePositions.contains(move.position()) ||
            Boolean.TRUE.equals(board.getBoardComplete()) ||
            !board.getPlayerOnTurn().equals(move.player())) {
            return false;
        }
        return board.getScores()
            .stream()
            .filter(score -> score.position().equals(move.position()))
            .findFirst()
            .isEmpty();
    }

    /**
     * verifies if the board is completed in case
     *
     * @param board
     * @return
     */
    protected boolean isBoardComplete(final Board board) {
        var listPositionsPlayerA = getPositions(board, Player.A);
        var listPositionsPlayerB = getPositions(board, Player.B);

        return isWinner(listPositionsPlayerA) || isWinner(listPositionsPlayerB) ||
            noMovesLeft(listPositionsPlayerA, listPositionsPlayerB);
    }

    private boolean isWinner(final List<Integer> listPositions) {
        for (List<Integer> completedPosition : completedPositions) {
            var result = listPositions.containsAll(completedPosition);
            if (result) {
                return true;
            }
        }
        return false;
    }

    private String getWinnerPlayer(final Board board) {
        if (isWinner(getPositions(board, Player.A))) {
            return Player.A.name();
        } else if (isWinner(getPositions(board, Player.B))) {
            return Player.B.name();
        }
        return Strings.EMPTY;
    }

    /**
     * verifies if all positions on the board are occupied by summing up all the position values
     * (1+2+3+4+5+6+7+8+9 = 45)
     *
     * @param listPositionsPlayerA
     * @param listPositionsPlayerB
     * @return
     */
    private boolean noMovesLeft(final List<Integer> listPositionsPlayerA, final List<Integer> listPositionsPlayerB) {
        var res01 = listPositionsPlayerA.stream().reduce(Integer::sum);
        var res02 = listPositionsPlayerB.stream().reduce(Integer::sum);
        return ((res01.orElse(0) + res02.orElse(0)) == 45);
    }

    private List<Integer> getPositions(final Board board, final Player player) {
        return board.getScores()
            .stream().filter(score -> score.player().equals(player.name()))
            .map(Score::position)
            .toList();
    }

    private String getNextPlayer(final Board board) {
        if (board.getPlayerOnTurn().equals(Player.A.name())) {
            return Player.B.name();
        }
        return Player.A.name();
    }

    private Player getRandomPlayer() {
        int randomBinary = random.nextInt(1000) % 2;
        return (randomBinary == 0 ? Player.A : Player.B);
    }
}
