package com.adsquare.tictactoe.repository;

import static com.adsquare.tictactoe.Util.boardId01;
import static com.adsquare.tictactoe.Util.boardId02;
import static com.adsquare.tictactoe.Util.getBoards;
import static com.adsquare.tictactoe.Util.getScoreBoard01;
import static com.adsquare.tictactoe.Util.getScoreBoard02;
import static com.adsquare.tictactoe.Util.getScoreBoard03;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.util.Player;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
class BoardRepositoryIntTest {

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        var board =
            boardRepository.saveAll(getBoards())
                .blockLast(); // ensure to be synchronous because tests must start only after this. only used for test.
    }

    @AfterEach
    void tearDown() {
        boardRepository.deleteAll().block();
    }

    @Test
    void findAllBoards() {
        // given

        // when
        final Flux<Board> boardFlux = boardRepository.findAll().log();

        // then
        StepVerifier.create(boardFlux)
            .expectNextCount(2)
            .verifyComplete();
    }

    @Test
    void findBoardById() {
        // given

        // when
        final Mono<Board> boardMono = boardRepository.findById(boardId01).log();

        // then
        StepVerifier.create(boardMono)
            .assertNext(board -> {
                assertEquals(boardId01, board.getId());
                assertEquals(Player.A.name(), board.getWinnerPlayer());
                assertEquals(true, board.getBoardComplete());
                assertEquals(7, board.getScores().size());
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(0)));
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(1)));
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(2)));
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(3)));
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(4)));
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(5)));
                assertThat(board.getScores(), hasItem(getScoreBoard01().get(6)));
            })
            .verifyComplete();
    }

    @Test
    void saveBoard() {
        // given
        var board = new Board(null, Player.B.name(), "", false, getScoreBoard03());

        // when
        final Mono<Board> boardMono = boardRepository.save(board).log();

        // then
        StepVerifier.create(boardMono)
            .assertNext(b -> {
                assertNotNull(b.getId());
                assertEquals(Player.B.name(), b.getPlayerOnTurn());
                assertEquals("", b.getWinnerPlayer());
                assertEquals(false, b.getBoardComplete());
                assertEquals(3, b.getScores().size());
                assertThat(b.getScores(), hasItem(getScoreBoard03().get(0)));
                assertThat(b.getScores(), hasItem(getScoreBoard03().get(1)));
                assertThat(b.getScores(), hasItem(getScoreBoard03().get(2)));
            })
            .verifyComplete();
    }

    @Test
    void updateBoard() {
        // given
        var newScore = new Score(Player.B.name(), 8);
        Board board = boardRepository.findById(boardId02).block();
        assert board != null;
        board.setPlayerOnTurn(Player.A.name());
        board.getScores().add(newScore);

        // when
        final Mono<Board> boardMono = boardRepository.save(board).log();

        // then
        StepVerifier.create(boardMono)
            .assertNext(b -> {
                assertNotNull(b.getId());
                assertEquals(Player.A.name(), b.getPlayerOnTurn());
                assertEquals("", b.getWinnerPlayer());
                assertEquals(false, b.getBoardComplete());
                assertEquals(4, b.getScores().size());
                assertThat(b.getScores(), hasItem(getScoreBoard02().get(0)));
                assertThat(b.getScores(), hasItem(getScoreBoard02().get(1)));
                assertThat(b.getScores(), hasItem(getScoreBoard02().get(2)));
                assertThat(b.getScores(), hasItem(newScore));
            })
            .verifyComplete();
    }

    @Test
    void deleteAllBoard() {
        // when
        boardRepository.deleteAll().block();

        final Flux<Board> boardFlux = boardRepository.findAll().log();

        // then
        StepVerifier.create(boardFlux)
            .expectNextCount(0)
            .verifyComplete();
    }
}
