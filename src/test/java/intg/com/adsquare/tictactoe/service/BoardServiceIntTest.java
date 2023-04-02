package com.adsquare.tictactoe.service;

import static com.adsquare.tictactoe.Util.boardId01;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.getBoard02;
import static com.adsquare.tictactoe.Util.getBoards;
import static com.mongodb.assertions.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;

@DataMongoTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
class BoardServiceIntTest {

    @Autowired
    private BoardRepository boardRepository;

    private BoardService boardService;

    private static Stream<Arguments> getPositionsUnavailable() {
        return Stream.of(
            Arguments.of(new Move(boardId02, Player.B.name(), 1)),
            Arguments.of(new Move(boardId02, Player.B.name(), 0)),
            Arguments.of(new Move(boardId02, Player.B.name(), 10)),
            Arguments.of(new Move(boardId02, Player.A.name(), 3)),
            Arguments.of(new Move(boardId01, Player.A.name(), 2))
        );
    }

    @BeforeEach
    void setUp() {
        boardService = new BoardService(boardRepository);
        boardRepository.saveAll(getBoards())
            .blockLast(); // ensure to be synchronous because tests must start only after this. only used for test.
    }

    @AfterEach
    void tearDown() {
        boardRepository.deleteAll().block();
    }

    @Test
    void positionsAvailable() {
        // given
        var move = new Move(boardId02, Player.B.name(), 3);

        // when
        var result = boardService.isPositionAvailable(getBoard02(), move);

        // then
        assertTrue(result);
    }

    @ParameterizedTest
    @MethodSource("getPositionsUnavailable")
    void positionsUnavailable(Move move) {
        // given
        // when
        var result = boardService.isPositionAvailable(getBoard02(), move);

        // then
        assertFalse(result);
    }
}
