package com.adsquare.tictactoe.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    @NotBlank(message = "boardId must be present")
    private String boardId;

    @NotBlank(message = "player must be present")
    private String player;

    @Min(value = 1, message = "position must be higher or equal to 1")
    @Max(value = 9, message = "position must be lower or equal to 9")
    private Integer position;
}
