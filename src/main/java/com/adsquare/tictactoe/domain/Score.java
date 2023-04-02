package com.adsquare.tictactoe.domain;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    private String player;

    private Integer position;

    private LocalTime timestamp;
}
