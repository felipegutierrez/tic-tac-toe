package com.adsquare.tictactoe.domain;

import java.util.Collection;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Board {

    @Id
    private String id;

    private String playerOnTurn;

    private String winnerPlayer;

    private Boolean boardComplete;

    private Collection<Score> scores;
}
