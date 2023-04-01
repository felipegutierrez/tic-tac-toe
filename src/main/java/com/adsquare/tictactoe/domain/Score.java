package com.adsquare.tictactoe.domain;

import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Score {

    @Id
    private String id;
    private String player;
    private Integer position;
    private LocalTime timestamp;
}
