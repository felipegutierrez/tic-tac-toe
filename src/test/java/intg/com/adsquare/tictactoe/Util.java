package com.adsquare.tictactoe;

import java.time.LocalTime;
import java.util.List;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.util.Player;

public class Util {

    public static final String boardId01 = "abc";
    public static final String boardId02 = "def";

    public static List<Board> getBoards() {
        return List.of(
            new Board(boardId01, Player.A.name(), Player.A.name(), true, getScoreBoard01()),
            new Board(boardId02, Player.B.name(), "", false, getScoreBoard02())
        );
    }
    public static List<Score> getScoreBoard01() {
        return List.of(
            new Score(null, Player.A.name(), 3, LocalTime.of(10, 10, 0)),
            new Score(null, Player.B.name(), 1, LocalTime.of(10, 10, 10)),
            new Score(null, Player.A.name(), 7, LocalTime.of(10, 10, 20)),
            new Score(null, Player.B.name(), 5, LocalTime.of(10, 10, 30)),
            new Score(null, Player.A.name(), 9, LocalTime.of(10, 10, 40)),
            new Score(null, Player.B.name(), 6, LocalTime.of(10, 10, 50)),
            new Score(null, Player.A.name(), 8, LocalTime.of(10, 11, 0))
        );
    }

    public static List<Score> getScoreBoard02() {
        return List.of(
            new Score(null, Player.A.name(), 5, LocalTime.of(11, 10, 0)),
            new Score(null, Player.B.name(), 1, LocalTime.of(11, 10, 10)),
            new Score(null, Player.A.name(), 7, LocalTime.of(11, 10, 20))
        );
    }

    public static List<Score> getScoreBoard03() {
        return List.of(
            new Score(null, Player.A.name(), 5, LocalTime.of(12, 10, 0)),
            new Score(null, Player.B.name(), 1, LocalTime.of(12, 10, 10)),
            new Score(null, Player.A.name(), 7, LocalTime.of(12, 10, 20))
        );
    }
}
