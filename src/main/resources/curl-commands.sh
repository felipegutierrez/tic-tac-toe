#### POST
curl -i \
-H "Content-Type: application/json" \
-X POST http://localhost:8080/v1/startboard

curl -i \
-H "Content-Type: application/json" \
-X POST http://localhost:8080/v1/deleteboards

#### PUT
curl -i \
-d '{"boardId":"642888e3ee7a2c65f96bcd0f", "player": "A", "position": 1}' \
-H "Content-Type: application/json" \
-X PUT http://localhost:8080/v1/playboard

#### GET
curl http://localhost:8080/v1/showboards | json_pp -json_opt pretty,canonical

