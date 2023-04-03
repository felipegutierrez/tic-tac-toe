package com.adsquare.tictactoe;

import java.util.List;

import org.apache.logging.log4j.util.Strings;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.util.Player;

public class Util {

    public static final String START_BOARD = "/v1/startboard";
    public static final String SHOW_BOARDS = "/v1/showboards";
    public static final String SHOW_BOARD = "/v1/showboard";
    public static final String DELETE_ALL_BOARD = "/v1/deleteboards";
    public static final String PLAY_BOARD = "/v1/playboard";

    public static final String boardId01 = "abc";
    public static final String boardId02 = "def";
    public static final String boardId03 = "ghi";
    public static final String boardId04 = "jkl";
    public static final String boardId05 = "mno";
    public static final String boardId06 = "pqr";
    public static final String boardId07 = "stu";

    public static List<Board> getBoards() {
        return List.of(getBoard01(), getBoard02());
    }

    public static Board getBoard01() {
        return new Board(boardId01, Player.A.name(), Player.A.name(), true, getScoreBoard01());
    }

    public static Board getBoard02() {
        return new Board(boardId02, Player.B.name(), Strings.EMPTY, false, getScoreBoard02());
    }

    public static Board getBoard03() {
        return new Board(boardId03, Player.B.name(), Strings.EMPTY, false, getScoreBoard03());
    }

    public static Board getBoard04() {
        return new Board(boardId04, Player.A.name(), Player.A.name(), true, getScoreBoard04());
    }

    public static Board getBoard05() {
        return new Board(boardId05, Player.B.name(), Player.B.name(), true, getScoreBoard05());
    }

    public static Board getBoard06() {
        return new Board(boardId06, Player.A.name(), Strings.EMPTY, true, getScoreBoard06());
    }

    public static Board getBoard07() {
        return new Board(boardId07, Player.B.name(), Strings.EMPTY, true, getScoreBoard07());
    }

    /**
     * |--A--|
     * |B,2,A|
     * |4,B,B|
     * |A,A,A|
     */
    public static List<Score> getScoreBoard01() {
        return List.of(
            new Score(Player.A.name(), 3),
            new Score(Player.B.name(), 1),
            new Score(Player.A.name(), 7),
            new Score(Player.B.name(), 5),
            new Score(Player.A.name(), 9),
            new Score(Player.B.name(), 6),
            new Score(Player.A.name(), 8)
        );
    }

    /**
     * |-----|
     * |B,2,3|
     * |4,A,6|
     * |A,8,9|
     */
    public static List<Score> getScoreBoard02() {
        return List.of(
            new Score(Player.A.name(), 5),
            new Score(Player.B.name(), 1),
            new Score(Player.A.name(), 7)
        );
    }

    /**
     * |-----|
     * |B,2,3|
     * |4,A,A|
     * |7,8,9|
     */
    public static List<Score> getScoreBoard03() {
        return List.of(
            new Score(Player.A.name(), 5),
            new Score(Player.B.name(), 1),
            new Score(Player.A.name(), 6)
        );
    }

    /**
     * |--A--|
     * |A,B,3|
     * |A,A,B|
     * |A,8,B|
     */
    public static List<Score> getScoreBoard04() {
        return List.of(
            new Score(Player.A.name(), 1),
            new Score(Player.B.name(), 2),
            new Score(Player.A.name(), 5),
            new Score(Player.B.name(), 9),
            new Score(Player.A.name(), 4),
            new Score(Player.B.name(), 6),
            new Score(Player.A.name(), 7)
        );
    }

    /**
     * |--B--|
     * |B,B,B|
     * |4,A,A|
     * |A,8,9|
     */
    public static List<Score> getScoreBoard05() {
        return List.of(
            new Score(Player.A.name(), 5),
            new Score(Player.B.name(), 1),
            new Score(Player.A.name(), 7),
            new Score(Player.B.name(), 3),
            new Score(Player.A.name(), 6),
            new Score(Player.B.name(), 2)
        );
    }

    /**
     * |-----|
     * |B,2,B|
     * |4,A,6|
     * |A,8,9|
     */
    public static List<Score> getScoreBoard06() {
        return List.of(
            new Score(Player.A.name(), 5),
            new Score(Player.B.name(), 1),
            new Score(Player.A.name(), 7),
            new Score(Player.B.name(), 3)
        );
    }

    /**
     * |-----|
     * |A,B,A|
     * |A,A,B|
     * |B,A,B|
     */
    public static List<Score> getScoreBoard07() {
        return List.of(
            new Score(Player.A.name(), 1),
            new Score(Player.B.name(), 2),
            new Score(Player.A.name(), 5),
            new Score(Player.B.name(), 9),
            new Score(Player.A.name(), 4),
            new Score(Player.B.name(), 6),
            new Score(Player.A.name(), 8),
            new Score(Player.B.name(), 7),
            new Score(Player.A.name(), 3)
        );
    }
}
