## planning
- spring boot app + webflux + mongoDB (docker) + gradle
- set up github project
- data model: a matrix 3x3 with positions and possible values
  - table name: board
    - columns:
      - id (int, PK)
      - turn (char, o or x user): to record which user is on the turn
      - winner (string, default empty): o or x
      - complete (boolean, default FALSE): TRUE when x or o wins, a new game is started, game machine is turned off.
  - table name: scores
    - columns:
      - id (int, PK)
      - board_id (int, FK from board)
      - turn (char, o or x user): the user who recorded this position
      - position (int, 1 to 9): the position set by the user
      - timestamp: to get the last player and the next turn

```text
# available positions
|-----|
|1,2,3|
|4,5,6|
|7,8,9|
|-----|

scores table
|board_id,turn|
|1    , x| -> |1, o    | -> |1, x    | -> |1, o    | -> |1, x    | -> |1, o    | -> |1, x    |
|--------| -> |--------| -> |--------| -> |--------| -> |--------| -> |--------| -> |--------|
|  |  |3x|    |1o|  |3x|    |1o|  |3x|    |1o|  |3x|    |1o|  |3x|    |1o|  |3x|    |1o|  |3x|
|  |  |  |    |  |  |  |    |  |  |  |    |  |5o|  |    |  |5o|  |    |  |5o|6o|    |  |5o|6o|
|  |  |  |    |  |  |  |    |7x|  |  |    |7x|  |  |    |7x|  |9x|    |7x|  |9x|    |7x|8x|9x|

# user `x` wins
table board
id, turn, winner, complete
0 , x   , x     , true
1 , o   ,       , false

table scores
id, board_id, turn, position, timestamp
0 , 0       , x   , 3       , 0000001
1 , 0       , o   , 1       , 0000002
2 , 0       , x   , 7       , 0000003
3 , 0       , o   , 5       , 0000004
4 , 0       , x   , 9       , 0000005
5 , 0       , o   , 6       , 0000006
6 , 0       , x   , 8       , 0000007
7 , 1       , o   , 5       , 0000008
8 , 1       , x   , 1       , 0000009
....
```
- controller model:
	- POST:
		- /start -> start a new game and returns the new 'board.id'.
		- /exit -> turns off the game machine.
		- /play {board.id: 0, turn: 'x', position: 3}
			-> possible returns:
				- board updated
				- position already filled
				- position doesn't exist
				- x or o wins the game -> game over
	- GET:
		- /status {board.id: 0} -> shows the board
- view model:
	- only curl commands for now
- services / business logic:
	- startBoard:
		- if there is a row in the `board` table with `complete = FALSE` we set it to `complete = TRUE`.
		- create a new row in the `board` table and an aleatory user (o or x) for the `turn` column.
		- `complete` column is false.
	- recordScore:
		- receives a board_id, a user, and its desired position
        - check if the board is `complete == false`
          - `complete == false`
            - get the user that is on the `turn` column.
            - check if the `turn` matches with the given user.
            - check if the position is available
              - TRUE:
                - creates a row in `score` table with `board_id`, `turn`, `position` (1 to 9), current timestamp
              - FALSE:
                - shows: position is occupied or does not exist
                - invites the same player to try again
          - `complete == true`
            - shows instruction to start the game
    - updateBoard:
      - check if one of the users have won the game
        - FALSE: invite the next player to send its curl command to the board.
        - TRUE: congratulates the winner and finalize the game.
	- status:
		- fetches data from DB and shows the current state of the board
	- exit:
		- if there is a row in the board table with `complete = FALSE` we set it to `complete = TRUE`
