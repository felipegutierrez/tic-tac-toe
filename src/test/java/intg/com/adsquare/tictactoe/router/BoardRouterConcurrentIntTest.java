package com.adsquare.tictactoe.router;

import static com.adsquare.tictactoe.Util.PLAY_BOARD_FUNC;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.getBoard02;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.StatusAssertions;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.repository.BoardRepository;
import com.adsquare.tictactoe.util.Player;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@Execution(ExecutionMode.CONCURRENT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BoardRouterConcurrentIntTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static Stream<Arguments> getMoves() {
        return Stream.of(
            Arguments.of(new Move(boardId02, Player.B, 2), false),
            Arguments.of(new Move(boardId02, Player.B, 3), false),
            Arguments.of(new Move(boardId02, Player.B, 4), false),
            Arguments.of(new Move(boardId02, Player.B, 6), false),
            Arguments.of(new Move(boardId02, Player.B, 8), false),
            Arguments.of(new Move(boardId02, Player.B, 9), false),
            Arguments.of(new Move(boardId02, Player.B, 2), true),
            Arguments.of(new Move(boardId02, Player.B, 3), false)
        );
    }

    @BeforeAll
    void setUp() {
        System.setProperty("reactor.netty.ioWorkerCount", "16");
        boardRepository.save(getBoard02())
            .block(); // ensure to be synchronous because tests must start only after this. only used for test.
    }

    @AfterAll
    void tearDown() {
        boardRepository.deleteAll().block();
    }

    /**
     * This test ensures the application can receive concurrent access but block concurrent updates on the board.
     * "WiredTiger uses document-level concurrency control for write operations. As a result, multiple clients can
     * modify different documents of a collection at the same time."
     * https://www.mongodb.com/docs/manual/core/wiredtiger/#document-level-concurrency
     *
     * @param move                 moves to be performed on the board with available position and always the same user B
     * @param isResponseBadRequest the response of the application.
     */
    @Disabled("Concurrent test is passing using './gradlew cleanTest test' but not on IntelliJ.")
    @ParameterizedTest
    @MethodSource("getMoves")
    void playBoard(final Move move, boolean isResponseBadRequest) {
        // given // when // then
        final StatusAssertions actualStatusAssertions = webTestClient.put()
            .uri(PLAY_BOARD_FUNC)
            .bodyValue(move)
            .exchange()
            .expectStatus();
        if (isResponseBadRequest) {
            actualStatusAssertions.isBadRequest();
        } else {
            actualStatusAssertions.isOk();
        }
    }
}
