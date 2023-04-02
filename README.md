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

## Playing
1. Create a new board and get the board ID
```shell
curl -i -H "Content-Type: application/json" -X POST http://localhost:8080/v1/startboard
```
```json
{"id":"6429b52726edc9050fa31a77","playerOnTurn":"A","winnerPlayer":"","boardComplete":false,"scores":[]}
```
2. Show all boards to recover you board ID, see who is the next player, check all positions and if there is a winner
```shell
curl http://localhost:8080/v1/showboards | json_pp -json_opt pretty,canonical
```
```json
[
   {
      "boardComplete" : false,
      "id" : "6429b52726edc9050fa31a77",
      "playerOnTurn" : "A",
      "scores" : [],
      "winnerPlayer" : ""
   }
]
```
3. register a move using the `playerOnTurn : A`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "A", "position": 3}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
4. register a move using the `playerOnTurn : B`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "B", "position": 1}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
5. register a move using the `playerOnTurn : A`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "A", "position": 7}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
6. register a move using the `playerOnTurn : B`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "B", "position": 5}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
7. register a move using the `playerOnTurn : A`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "A", "position": 9}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
8. register a move using the `playerOnTurn : B`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "B", "position": 6}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
9. register a move using the `playerOnTurn : A`
```shell
curl -i \
-d '{"boardId":"6429b52726edc9050fa31a77", "player": "A", "position": 8}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard
```
10. `playerOnTurn : A` wins and you cannot register further moves. Check the board again.
```shell
curl http://localhost:8080/v1/showboards | json_pp -json_opt pretty,canonical
```
11. delete all boards
```shell
curl -i \
-H "Content-Type: application/json" \
-X POST http://localhost:8080/v1/deleteboards
```
## Tests
```shell
./gradlew test
```

