package com.adsquare.tictactoe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.service.BoardService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class BoardController {

    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/startboard")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Board> startBoard() {

        boardService.terminateAllBoards();

        return boardService.startNewBoard().log();
    }

    @GetMapping("/showboards")
    public Flux<Board> getAllBoards() {
        return boardService.getAllBoards();
    }
}
