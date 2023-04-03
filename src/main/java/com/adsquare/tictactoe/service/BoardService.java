package com.adsquare.tictactoe.service;

import static com.adsquare.tictactoe.util.TicTacToeRules.availablePositions;
import static com.adsquare.tictactoe.util.TicTacToeRules.completedPositions;
import static com.adsquare.tictactoe.util.TicTacToeRules.getNextPlayer;
import static com.adsquare.tictactoe.util.TicTacToeRules.getRandomPlayer;

import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.exception.BoardException;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public Mono<Board> startNewBoard() {
        return boardRepository.save(
            new Board(null, getRandomPlayer().name(), Strings.EMPTY, false, List.of())
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
            .flatMap(board -> {
                if (!board.getPlayerOnTurn().equals(move.player())) {
                    return Mono.error(new BoardException("It is not the turn of player " + move.player()));
                }
                if (!isPositionAvailable(board, move)) {
                    return Mono.error(new BoardException("Position " + move.position() + " is not available"));
                }
                board.getScores().add(new Score(move.player(), move.position()));
                if (isBoardComplete(board)) {
                    board.setPlayerOnTurn(Strings.EMPTY);
                    board.setBoardComplete(true);
                    board.setWinnerPlayer(getWinnerPlayer(board));
                } else {
                    board.setPlayerOnTurn(getNextPlayer(board));
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
            !areThereMovesLeft(listPositionsPlayerA, listPositionsPlayerB);
    }

    private boolean isWinner(final List<Integer> listPositions) {
        for (List<Integer> completedPosition : completedPositions) {
            var result = new HashSet<>(listPositions).containsAll(completedPosition);
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
     * @param positionsPlayerA
     * @param positionsPlayerB
     * @return
     */
    private boolean areThereMovesLeft(final List<Integer> positionsPlayerA, final List<Integer> positionsPlayerB) {
        var res01 = positionsPlayerA.stream().reduce(Integer::sum);
        var res02 = positionsPlayerB.stream().reduce(Integer::sum);
        return ((res01.orElse(0) + res02.orElse(0)) < 45);
    }

    private List<Integer> getPositions(final Board board, final Player player) {
        return board.getScores()
            .stream().filter(score -> score.player().equals(player.name()))
            .map(Score::position)
            .toList();
    }
}
