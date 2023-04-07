package com.adsquare.tictactoe.router;

import static com.adsquare.tictactoe.Util.DELETE_ALL_BOARDS_FUNC;
import static com.adsquare.tictactoe.Util.DELETE_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.PLAY_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.SHOW_BOARDS_FUNC;
import static com.adsquare.tictactoe.Util.SHOW_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.START_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.boardId07;
import static com.adsquare.tictactoe.Util.getBoards;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import com.adsquare.tictactoe.domain.Move;
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
                assert (newBoard.getPlayerOnTurn().equals(Player.A) || newBoard.getPlayerOnTurn().equals(Player.B));
                assert newBoard.getWinnerPlayer() == null;
                assert newBoard.getScores().size() == 0;
            });
    }

    @Test
    void getAllBoards() {
        // when // then
        webTestClient.get()
            .uri(SHOW_BOARDS_FUNC)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class)
            .hasSize(2);
    }

    @Test
    void getBoard() {
        // given
        // when // then
        webTestClient.get()
            .uri(SHOW_BOARD_FUNC + "/{id}", boardId02)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Board.class)
            .consumeWith(boardEntityExchangeResult -> {
                var b = boardEntityExchangeResult.getResponseBody();
                assert b != null;
                assert b.getScores().size() == 3;
            });
    }

    @Test
    void getBoardWithEmptyId() {
        // given
        // when // then
        webTestClient.get()
            .uri(SHOW_BOARD_FUNC + "/{id}", Strings.EMPTY)
            .exchange()
            .expectStatus()
            .is4xxClientError()
            .expectBody(Board.class);
    }

    @Test
    void getBoardNotFound() {
        // given
        // when // then
        webTestClient.get()
            .uri(SHOW_BOARD_FUNC + "/{id}", boardId07)
            .exchange()
            .expectStatus()
            .isNotFound()
            .expectBody(String.class)
            .isEqualTo("Board " + boardId07 + " not found");
    }

    @Test
    void deleteAllBoards() {
        // when // then
        webTestClient.delete()
            .uri(DELETE_ALL_BOARDS_FUNC)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class)
            .hasSize(0);
    }

    @Test
    void deleteBoard() {
        // when // then
        webTestClient.delete()
            .uri(DELETE_BOARD_FUNC + "/{id}", boardId02)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class)
            .hasSize(0);
    }

    @Test
    void playNonExistingBoard() {
        // given
        var move = new Move("non-existing-board", Player.B, 3);
        // when // then
        webTestClient.put()
            .uri(PLAY_BOARD_FUNC)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .isEqualTo("Board not found");
    }

    @Test
    void playBoard() {
        // given
        var move = new Move(boardId02, Player.B, 3);
        // when // then
        webTestClient.put()
            .uri(PLAY_BOARD_FUNC)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class);
        webTestClient.get()
            .uri(SHOW_BOARD_FUNC + "/{id}", boardId02)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBody(Board.class)
            .consumeWith(boardEntityExchangeResult -> {
                var b = boardEntityExchangeResult.getResponseBody();
                assert b != null;
                assert b.getScores().size() == 4;
            });
    }
}
