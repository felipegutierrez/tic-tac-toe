package com.adsquare.tictactoe.util;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.domain.Score;

@Service
public class TicTacToeRules {

    // all available positions on the tic-tac-toe board
    public final List<Integer> availablePositions;
    // all possible positions combinations that make the tic-tac-toe game end with a winner
    public final List<List<Integer>> completedPositions;
    private final Random random;

    public TicTacToeRules() {
        this.random = new Random();
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
    }

    public Player getRandomPlayer() {
        int randomBinary = random.nextInt(1000) % 2;
        return (randomBinary == 0 ? Player.A : Player.B);
    }

    public Player getNextPlayer(final Board board) {
        return (board.getPlayerOnTurn().equals(Player.A) ? Player.B : Player.A);
    }

    /**
     * Verifies if a given position is available in the giver board
     *
     * @param board
     * @param move
     * @return
     */
    public boolean isPositionAvailable(final Board board, final Move move) {
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
    public boolean isBoardComplete(final Board board) {
        var listPositionsPlayerA = getPositions(board, Player.A);
        var listPositionsPlayerB = getPositions(board, Player.B);

        return isWinner(listPositionsPlayerA) || isWinner(listPositionsPlayerB) ||
            !areThereMovesLeft(listPositionsPlayerA, listPositionsPlayerB);
    }

    public boolean isWinner(final List<Integer> listPositions) {
        for (List<Integer> completedPosition : completedPositions) {
            var result = new HashSet<>(listPositions).containsAll(completedPosition);
            if (result) {
                return true;
            }
        }
        return false;
    }

    public Player getWinnerPlayer(final Board board) {
        if (isWinner(getPositions(board, Player.A))) {
            return Player.A;
        } else if (isWinner(getPositions(board, Player.B))) {
            return Player.B;
        }
        return null;
    }

    /**
     * verifies if all positions on the board are occupied by summing up all the position values
     * (1+2+3+4+5+6+7+8+9 = 45)
     *
     * @param positionsPlayerA
     * @param positionsPlayerB
     * @return
     */
    public boolean areThereMovesLeft(final List<Integer> positionsPlayerA, final List<Integer> positionsPlayerB) {
        var res01 = positionsPlayerA.stream().reduce(Integer::sum);
        var res02 = positionsPlayerB.stream().reduce(Integer::sum);
        return ((res01.orElse(0) + res02.orElse(0)) < 45);
    }

    public List<Integer> getPositions(final Board board, final Player player) {
        return board.getScores()
            .stream()
            .filter(score -> score.player().equals(player))
            .map(Score::position)
            .toList();
    }
}
