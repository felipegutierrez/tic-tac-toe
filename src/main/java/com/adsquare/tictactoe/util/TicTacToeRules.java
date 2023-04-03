package com.adsquare.tictactoe.util;

import java.util.List;
import java.util.Random;

import com.adsquare.tictactoe.domain.Board;

public class TicTacToeRules {

    // all available positions on the tic-tac-toe board
    public static final List<Integer> availablePositions = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
    // all possible positions combinations that make the tic-tac-toe game end with a winner
    public static final List<List<Integer>> completedPositions = List.of(
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
    private static final Random random = new Random();

    private TicTacToeRules() {
    }

    public static Player getRandomPlayer() {
        int randomBinary = random.nextInt(1000) % 2;
        return (randomBinary == 0 ? Player.A : Player.B);
    }

    public static String getNextPlayer(final Board board) {
        if (board.getPlayerOnTurn().equals(Player.A.name())) {
            return Player.B.name();
        }
        return Player.A.name();
    }
}
