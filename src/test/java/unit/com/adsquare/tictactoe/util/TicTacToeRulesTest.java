package com.adsquare.tictactoe.util;

import static com.adsquare.tictactoe.Util.boardId01;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.getBoard01;
import static com.adsquare.tictactoe.Util.getBoard02;
import static com.adsquare.tictactoe.Util.getBoard03;
import static com.adsquare.tictactoe.Util.getBoard04;
import static com.adsquare.tictactoe.Util.getBoard05;
import static com.adsquare.tictactoe.Util.getBoard06;
import static com.adsquare.tictactoe.Util.getBoard07;
import static com.mongodb.assertions.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TicTacToeRulesTest {

    private TicTacToeRules ticTacToeRules;

    private static Stream<Arguments> getPositionsUnavailable() {
        return Stream.of(
            Arguments.of(new Move(boardId02, Player.B.name(), 1)),
            Arguments.of(new Move(boardId02, Player.B.name(), 0)),
            Arguments.of(new Move(boardId02, Player.B.name(), 10)),
            Arguments.of(new Move(boardId02, Player.A.name(), 3)),
            Arguments.of(new Move(boardId01, Player.A.name(), 2))
        );
    }

    private static Stream<Arguments> getCompletedBoards() {
        return Stream.of(
            Arguments.of(getBoard01()),
            Arguments.of(getBoard04()),
            Arguments.of(getBoard05()),
            Arguments.of(getBoard07())
        );
    }

    private static Stream<Arguments> getUncompletedBoards() {
        return Stream.of(
            Arguments.of(getBoard02()),
            Arguments.of(getBoard03()),
            Arguments.of(getBoard06())
        );
    }

    @BeforeAll
    void setUp() {
        ticTacToeRules = new TicTacToeRules();
    }

    @Test
    void positionsAvailable() {
        // given
        var move = new Move(boardId02, Player.B.name(), 3);

        // when
        var result = ticTacToeRules.isPositionAvailable(getBoard02(), move);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("getPositionsUnavailable")
    void positionsUnavailable(Move move) {
        // given
        // when
        var result = ticTacToeRules.isPositionAvailable(getBoard02(), move);

        // then
        assertFalse(result);
    }

    @ParameterizedTest
    @MethodSource("getCompletedBoards")
    void completedBoards(final Board board) {
        // given
        // when
        var result = ticTacToeRules.isBoardComplete(board);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("getUncompletedBoards")
    void uncompletedBoards(final Board board) {
        // given
        // when
        var result = ticTacToeRules.isBoardComplete(board);

        // then
        Assertions.assertFalse(result);
    }
}