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
                        board.setPlayerOnTurn(getNextPlayer(board));
                        board.getScores().add(new Score(move.getPlayer(), move.getPosition(), LocalTime.now()));
                        if (isBoardComplete(board)) {
                            board.setBoardComplete(true);
                            board.setWinnerPlayer(getWinnerPlayer(board));
                        }
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

    protected boolean isBoardComplete(final Board board) {
        var listPositionsPlayerA = getPositions(board, Player.A);
        var listPositionsPlayerB = getPositions(board, Player.B);

        return isWinner(listPositionsPlayerA) || isWinner(listPositionsPlayerB);
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
        return "";
    }

    private List<Integer> getPositions(final Board board, final Player player) {
        return board.getScores()
            .stream().filter(score -> score.getPlayer().equals(player.name()))
            .map(Score::getPosition)
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
