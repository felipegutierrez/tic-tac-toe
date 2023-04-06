package com.adsquare.tictactoe.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.adsquare.tictactoe.util.Player;
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

    private Player playerOnTurn;

    private Player winnerPlayer;

    private Boolean boardComplete;

    private List<Score> scores;
}
