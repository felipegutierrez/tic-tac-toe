package com.adsquare.tictactoe;

import java.util.List;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Score;
import com.adsquare.tictactoe.util.Player;

public class Util {

    // version 1 is the very common Restful api
    public static final String START_BOARD = "/v1/startboard";
    public static final String SHOW_BOARDS = "/v1/showboards";
    public static final String SHOW_BOARD = "/v1/showboard";
    public static final String DELETE_ALL_BOARDS = "/v1/deleteboards";
    public static final String PLAY_BOARD = "/v1/playboard";
    // version 2 is a Restful api using functional approach
    public static final String START_BOARD_FUNC = "/v2/startboard";
    public static final String SHOW_BOARDS_FUNC = "/v2/showboards";
    public static final String SHOW_BOARD_FUNC = "/v2/showboard";
    public static final String DELETE_ALL_BOARDS_FUNC = "/v2/deleteboards";
    public static final String DELETE_BOARD_FUNC = "/v2/deleteboard";
    public static final String PLAY_BOARD_FUNC = "/v2/playboard";

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
        return new Board(boardId01, Player.A, Player.A, true, getScoreBoard01());
    }

    public static Board getBoard02() {
        return new Board(boardId02, Player.B, null, false, getScoreBoard02());
    }

    public static Board getBoard03() {
        return new Board(boardId03, Player.B, null, false, getScoreBoard03());
    }

    public static Board getBoard04() {
        return new Board(boardId04, Player.A, Player.A, true, getScoreBoard04());
    }

    public static Board getBoard05() {
        return new Board(boardId05, Player.B, Player.B, true, getScoreBoard05());
    }

    public static Board getBoard06() {
        return new Board(boardId06, Player.A, null, true, getScoreBoard06());
    }

    public static Board getBoard07() {
        return new Board(boardId07, Player.B, null, true, getScoreBoard07());
    }

    /**
     * |--A--|
     * |B,2,A|
     * |4,B,B|
     * |A,A,A|
     */
    public static List<Score> getScoreBoard01() {
        return List.of(
            new Score(Player.A, 3),
            new Score(Player.B, 1),
            new Score(Player.A, 7),
            new Score(Player.B, 5),
            new Score(Player.A, 9),
            new Score(Player.B, 6),
            new Score(Player.A, 8)
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
            new Score(Player.A, 5),
            new Score(Player.B, 1),
            new Score(Player.A, 7)
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
            new Score(Player.A, 5),
            new Score(Player.B, 1),
            new Score(Player.A, 6)
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
            new Score(Player.A, 1),
            new Score(Player.B, 2),
            new Score(Player.A, 5),
            new Score(Player.B, 9),
            new Score(Player.A, 4),
            new Score(Player.B, 6),
            new Score(Player.A, 7)
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
            new Score(Player.A, 5),
            new Score(Player.B, 1),
            new Score(Player.A, 7),
            new Score(Player.B, 3),
            new Score(Player.A, 6),
            new Score(Player.B, 2)
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
            new Score(Player.A, 5),
            new Score(Player.B, 1),
            new Score(Player.A, 7),
            new Score(Player.B, 3)
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
            new Score(Player.A, 1),
            new Score(Player.B, 2),
            new Score(Player.A, 5),
            new Score(Player.B, 9),
            new Score(Player.A, 4),
            new Score(Player.B, 6),
            new Score(Player.A, 8),
            new Score(Player.B, 7),
            new Score(Player.A, 3)
        );
    }
}
