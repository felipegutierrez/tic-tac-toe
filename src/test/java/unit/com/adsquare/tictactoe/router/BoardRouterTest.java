package com.adsquare.tictactoe.router;

import static com.adsquare.tictactoe.Util.PLAY_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.boardId02;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.exception.GlobalFunctionalErrorHandler;
import com.adsquare.tictactoe.handler.BoardHandler;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;
import com.adsquare.tictactoe.util.TicTacToeRules;
import reactor.core.publisher.Mono;

@WebFluxTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes =
    { BoardRouter.class, BoardHandler.class, TicTacToeRules.class, GlobalFunctionalErrorHandler.class }
)
class BoardRouterTest {

    @MockBean
    private BoardRepository boardRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static Stream<Arguments> getWrongMoves() {
        return Stream.of(
            Arguments.of(new Move(boardId02, Player.B, 0), "position must be higher or equal to 1"),
            Arguments.of(new Move(boardId02, Player.B, -1), "position must be higher or equal to 1"),
            Arguments.of(new Move(boardId02, Player.B, 10), "position must be lower or equal to 9"),
            Arguments.of(new Move(boardId02, null, 1), "player equals 'null' is not valid"),
            Arguments.of(new Move(boardId02, Player.A, 1), "It is not the turn of player A")
        );
    }

    @Test
    void playBoard() {
        // given
        var move = new Move(boardId02, Player.B, 3);
        var board = new Board(boardId02, Player.B, null, false, new ArrayList<>());

        // when
        when(boardRepository.findById(isA(String.class))).thenReturn(Mono.just(board));
        when(boardRepository.save(isA(Board.class))).thenReturn(Mono.just(board));

        // then
        webTestClient.put()
            .uri(PLAY_BOARD_FUNC)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .is2xxSuccessful()
            .expectBodyList(Board.class);
    }

    @ParameterizedTest
    @MethodSource("getWrongMoves")
    void playBoardWithWrongMove(final Move move, final String exceptionMessage) {
        // given
        var board = new Board(boardId02, Player.B, null, false, new ArrayList<>());

        // when
        when(boardRepository.findById(isA(String.class))).thenReturn(Mono.just(board));

        // then
        webTestClient.put()
            .uri(PLAY_BOARD_FUNC)
            .bodyValue(move)
            .exchange()
            .expectStatus()
            .isBadRequest()
            .expectBody(String.class)
            .isEqualTo(exceptionMessage);
    }
}
