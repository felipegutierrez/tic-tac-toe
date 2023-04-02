package com.adsquare.tictactoe.service;

import static com.adsquare.tictactoe.Util.getBoard01;
import static com.adsquare.tictactoe.Util.getBoard02;
import static com.adsquare.tictactoe.Util.getBoard03;
import static com.adsquare.tictactoe.Util.getBoard04;
import static com.adsquare.tictactoe.Util.getBoard05;
import static com.adsquare.tictactoe.Util.getBoard06;
import static com.adsquare.tictactoe.Util.getBoard07;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.repository.BoardRepository;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoardServiceTest {

    private BoardService boardService;

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

    @BeforeEach
    void setUp() {
        boardService = new BoardService(mock(BoardRepository.class));
    }

    @ParameterizedTest
    @MethodSource("getCompletedBoards")
    void completedBoards(final Board board) {
        // given
        // when
        var result = boardService.isBoardComplete(board);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("getUncompletedBoards")
    void uncompletedBoards(final Board board) {
        // given
        // when
        var result = boardService.isBoardComplete(board);

        // then
        assertFalse(result);
    }
}