package com.adsquare.tictactoe.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.adsquare.tictactoe.domain.Board;
import com.adsquare.tictactoe.domain.Move;
import com.adsquare.tictactoe.service.BoardService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping("/startboard")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Board> startBoard() {

        return boardService.startNewBoard().log();
    }

    @PutMapping("/playboard")
    public Mono<ResponseEntity<Board>> playBoard(@RequestBody @Valid Move move) {

        return boardService.updateBoard(move)
            .map(ResponseEntity.ok()::body)
            .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND))
            .onErrorReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST))
            .log();
    }

    @GetMapping("/showboards")
    public Flux<Board> getAllBoards() {

        return boardService.getAllBoards().log();
    }

    @GetMapping("/showboard/{id}")
    public Mono<Board> getBoard(@PathVariable String id) {

        return boardService.findBoard(id)
            .log();
    }

    @PostMapping("/deleteboards")
    @ResponseStatus(HttpStatus.OK)
    public Mono<Void> deleteAllBoard() {

        return boardService.deleteAllBoards().log();
    }
}
