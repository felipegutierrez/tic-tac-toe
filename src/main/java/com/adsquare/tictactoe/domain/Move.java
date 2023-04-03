package com.adsquare.tictactoe.domain;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public record Move(
    @NotBlank(message = "boardId must be present")
    String boardId,

    @NotBlank(message = "player must be present")
    String player,

    @Min(value = 1, message = "position must be higher or equal to 1")
    @Max(value = 9, message = "position must be lower or equal to 9")
    Integer position
) {
}
