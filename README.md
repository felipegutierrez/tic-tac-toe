# Tic Tac Toe

## About
The game has some basic rules:
1. There are 2 players
2. Every player is represented by a unique symbol, usually it's X and O
3. The board consists of a 3x3 matrix. A player wins if they can align 3 of their markers in a vertical, horizontal or diagonal line
4. If no more moves are possible, the game should finish

There are some things to be considered for the design of the API:
* It might be possible that some calls need be protected against concurrent access 
* The game state should be persisted, use some DB backing for that 
* It is recommended to use Spring (Boot) for the implementation

Here is the project [plan](plan.md).

## Tech used
* Java 17
* Spring-boot 2.7
* MongoDB (Docker)

## Deploying
### Locally
* start MongoDB using the docker image
```
cd mongodb
docker-compose up -d
docker ps
```
* start the Tic-tac-toe application from the terminal
```
./gradlew bootRun
```
* or start the Tic=tac-toe application from IntelliJ [TicTacToeApplication.java](src/main/java/com/adsquare/tictactoe/TicTacToeApplication.java).
* stop MongoDB
```
docker-compose stop
docker-compose down
```
## Tests
```
./gradlew test
```

