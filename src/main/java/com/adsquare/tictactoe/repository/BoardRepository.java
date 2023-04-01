package com.adsquare.tictactoe.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.adsquare.tictactoe.domain.Board;

public interface BoardRepository extends ReactiveMongoRepository<Board, String> {
}
