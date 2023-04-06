package com.adsquare.tictactoe.controller;

import static com.adsquare.tictactoe.Util.DELETE_ALL_BOARDS;
import static com.adsquare.tictactoe.Util.PLAY_BOARD;
import static com.adsquare.tictactoe.Util.SHOW_BOARD;
import static com.adsquare.tictactoe.Util.SHOW_BOARDS;
import static com.adsquare.tictactoe.Util.START_BOARD;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.getBoards;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardControllerIntTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        boardRepository.saveAll(getBoards())
            .blockLast(); // ensure to be synchronous because tests must start only after this. only used for test.
    }

    @AfterEach
    void tearDown() {
        boardRepository.deleteAll().block();
    }

    @Test
    void startBoard() {
        // given

        // when // then
        webTestClient.post()
            .uri(START_BOARD)
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(Board.class)
            .consumeWith(boardEntityExchangeResult -> {
                var newBoard = boardEntityExchangeResult.getResponseBody();
                assert newBoard.getId() != null;
                assert !newBoard.getBoardComplete();
                assert (newBoard.getPlayerOnTurn().equals(Player.A.name()) ||
                    newBoard.getPlayerOnTurn().equals(Player.B.name()));
                assert newBoard.getWinnerPlayer().equals(Strings.EMPTY);
                assert newBoard.getScores().size() == 0;
            });
    }

    @Test
    void getAllBoards() {
        // when // then
        webTestClient.get()
            .uri(SHOW_BOARDS)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class)
            .hasSize(2);
    }

    @Test
    void playBoard() {
        // given
        var move = new Move(boardId02, Player.B.name(), 3);
        // when // then
        webTestClient.put()
            .uri(PLAY_BOARD)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class);
        webTestClient.get()
            .uri(SHOW_BOARD + "/{id}", boardId02)
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

    @Test
    void playNonExistingBoard() {
        // given
        var move = new Move("non-existing-board", Player.B.name(), 3);
        // when // then
        webTestClient.put()
            .uri(PLAY_BOARD)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .isBadRequest();
    }

    @Test
    void deleteAllBoards() {
        // when // then
        webTestClient.delete()
            .uri(DELETE_ALL_BOARDS)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class)
            .hasSize(0);
    }
}
