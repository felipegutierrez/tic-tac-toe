package com.adsquare.tictactoe.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    private String boardId;
    private String player;
    private Integer position;
}
