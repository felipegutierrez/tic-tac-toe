package com.adsquare.tictactoe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.mongodb.embedded.version=4.0.21")
@SpringBootTest
class TicTacToeApplicationTests {

	@Test
	void contextLoads() {
	}

}
