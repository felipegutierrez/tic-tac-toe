#### POST
curl -i \
-H "Content-Type: application/json" \
-X POST http://localhost:8080/v1/startboard

#### GET
curl http://localhost:8080/v1/showboards | json_pp -json_opt pretty,canonical

