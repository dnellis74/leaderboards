## To create
```shell
curl -X 'POST' \
  'http://localhost:5000/tournament' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{ "name" : "spring sale" }'
```
## To read
```shell
curl -X 'GET' \
  'http://localhost:5000/tournament' \
  -H 'accept: */*'
```