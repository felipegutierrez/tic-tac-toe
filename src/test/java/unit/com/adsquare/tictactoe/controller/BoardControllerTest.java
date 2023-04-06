package com.adsquare.tictactoe.controller;

import static com.adsquare.tictactoe.Util.PLAY_BOARD;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.getBoard02;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.service.BoardService;
import com.adsquare.tictactoe.util.Player;
import reactor.core.publisher.Mono;

@AutoConfigureWebTestClient
@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = BoardController.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BoardControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private BoardService boardService;

    private static Stream<Arguments> getCompletedBoards() {
        return Stream.of(
            Arguments.of(new Move(boardId02, Player.A, 0), "position must be higher or equal to 1"),
            Arguments.of(new Move(boardId02, null, 1), "player must be present"),
            Arguments.of(new Move(boardId02, Player.A, 0), "position must be higher or equal to 1"),
            Arguments.of(new Move(boardId02, Player.A, 10), "position must be lower or equal to 9"),
            Arguments.of(new Move(Strings.EMPTY, Player.A, 9), "boardId must be present")
        );
    }

    @ParameterizedTest
    @MethodSource("getCompletedBoards")
    void playBoardBadRequest(final Move move, final String expectedErrorMsg) {
        // given
        // when
        when(boardService.updateBoard(any())).thenReturn(Mono.just(getBoard02()));

        // then
        webTestClient.put()
            .uri(PLAY_BOARD)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .consumeWith(stringEntityExchangeResult -> {
                final String responseBody = stringEntityExchangeResult.getResponseBody();
                System.out.println(responseBody);
                assertEquals(expectedErrorMsg, responseBody);
            });
    }
}