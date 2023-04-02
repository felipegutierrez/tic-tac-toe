package com.adsquare.tictactoe.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.adsquare.tictactoe.domain.Score;

public interface ScoreRepository extends ReactiveMongoRepository<Score, String> {
}
