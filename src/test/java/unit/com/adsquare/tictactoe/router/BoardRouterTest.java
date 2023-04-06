package com.adsquare.tictactoe.router;

import static com.adsquare.tictactoe.Util.PLAY_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.boardId02;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.handler.BoardHandler;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;
import com.adsquare.tictactoe.util.TicTacToeRules;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = { BoardRouter.class, BoardHandler.class, TicTacToeRules.class })
@AutoConfigureWebTestClient
class BoardRouterTest {

    @MockBean
    private BoardRepository boardRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void playBoard() {
        // given
        var move = new Move(boardId02, Player.B.name(), 3);
        var board = new Board(boardId02, Player.B.name(), Strings.EMPTY, false, new ArrayList<>());

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
}
