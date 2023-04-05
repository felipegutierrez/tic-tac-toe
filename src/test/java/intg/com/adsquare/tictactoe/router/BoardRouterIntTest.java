package com.adsquare.tictactoe.router;

import static com.adsquare.tictactoe.Util.START_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.getBoards;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardRouterIntTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static Stream<Arguments> getPossibleBoardIds() {
        return Stream.of(Arguments.of(Strings.EMPTY), Arguments.of("yet-another-board-id-01"));
    }

    @BeforeEach
    void setUp() {
        boardRepository.saveAll(getBoards())
            .blockLast(); // ensure to be synchronous because tests must start only after this. only used for test.
    }

    @AfterEach
    void tearDown() {
        boardRepository.deleteAll().block();
    }

    @ParameterizedTest
    @MethodSource("getPossibleBoardIds")
    void startBoard(String boardId) {
        // given

        // when // then
        webTestClient.post()
            .uri(START_BOARD_FUNC)
            .bodyValue(boardId)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Board.class)
            .consumeWith(boardEntityExchangeResult -> {
                var newBoard = boardEntityExchangeResult.getResponseBody();
                assert newBoard.getId() != null;
                assertTrue(newBoard.getId().equals(boardId) || !newBoard.getId().equals(Strings.EMPTY));
                assert !newBoard.getBoardComplete();
                assert (newBoard.getPlayerOnTurn().equals(Player.A.name()) ||
                    newBoard.getPlayerOnTurn().equals(Player.B.name()));
                assert newBoard.getWinnerPlayer().equals(Strings.EMPTY);
                assert newBoard.getScores().size() == 0;
            });
    }
}