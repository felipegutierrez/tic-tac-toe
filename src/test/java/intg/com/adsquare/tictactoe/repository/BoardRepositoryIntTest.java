package com.adsquare.tictactoe.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Score;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
class BoardRepositoryIntTest {

    private final String boardId01 = "abc";
    private final String boardId02 = "def";

    @Autowired
    private BoardRepository boardRepository;

    @BeforeEach
    void setUp() {
        var board = List.of(
            new Board(boardId01, "x", "x", true, getScoreBoard01()),
            new Board(boardId02, "o", "", false, getScoreBoard02())
        );
        boardRepository.saveAll(board)
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
                assertEquals("x", board.getWinnerPlayer());
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
        var board = new Board(null, "o", "", false, getScoreBoard03());

        // when
        final Mono<Board> boardMono = boardRepository.save(board).log();

        // then
        StepVerifier.create(boardMono)
            .assertNext(b -> {
                assertNotNull(b.getId());
                assertEquals("o", b.getPlayerOnTurn());
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
        var newScore = new Score(null, "o", 8, LocalTime.of(11, 10, 30));
        Board board = boardRepository.findById(boardId02).block();
        assert board != null;
        board.setPlayerOnTurn("x");
        board.getScores().add(newScore);

        // when
        final Mono<Board> boardMono = boardRepository.save(board).log();

        // then
        StepVerifier.create(boardMono)
            .assertNext(b -> {
                assertNotNull(b.getId());
                assertEquals("x", b.getPlayerOnTurn());
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

    private List<Score> getScoreBoard01() {
        return List.of(
            new Score(null, "x", 3, LocalTime.of(10, 10, 0)),
            new Score(null, "o", 1, LocalTime.of(10, 10, 10)),
            new Score(null, "x", 7, LocalTime.of(10, 10, 20)),
            new Score(null, "o", 5, LocalTime.of(10, 10, 30)),
            new Score(null, "x", 9, LocalTime.of(10, 10, 40)),
            new Score(null, "o", 6, LocalTime.of(10, 10, 50)),
            new Score(null, "x", 8, LocalTime.of(10, 11, 0))
        );
    }

    private List<Score> getScoreBoard02() {
        return List.of(
            new Score(null, "x", 5, LocalTime.of(11, 10, 0)),
            new Score(null, "o", 1, LocalTime.of(11, 10, 10)),
            new Score(null, "x", 7, LocalTime.of(11, 10, 20))
        );
    }

    private List<Score> getScoreBoard03() {
        return List.of(
            new Score(null, "x", 5, LocalTime.of(12, 10, 0)),
            new Score(null, "o", 1, LocalTime.of(12, 10, 10)),
            new Score(null, "x", 7, LocalTime.of(12, 10, 20))
        );
    }
}
