# Range is Hot Leaderboard API
## Open API docs
http://localhost:5000/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config
## To create a tournament
```shell
curl -X 'POST' \
curl --location --request POST 'http://localhost:5000/tournament' \
--header 'Content-Type: application/json' \
--data-raw '{ 
    "name" : "inaugural tournament",
    "open" : "2021-07-01",
    "close" : "2021-07-07",
    "courses" : ["earth", "wind", "water", "fire"]
}'
```
## To read
```shell
curl -X 'GET' \
  'http://localhost:5000/tournament/1' \
  -H 'accept: */*'
```

## to submit a score
```shell
curl --location --request POST 'http://localhost:5000/tournament/1' \
--header 'ticket: 140000007a415805d650abe3ddfe88000100100196218260180000000100000002000000f5b2e3e55a460d56dd0f470003000000b20000003200000004000000ddfe88000100100166f116006528784c0c00000a000000009704776017b492600100e61608000000000074505af324ab491efec1618aeb83aed855b6afbf36e0526109c1af3931a13579be05904c97ade0e6a8011e066b38366fb198963afa0d2b5b10eedcd544365b0e90d8f9e8427a36e5291faaadb91711694460956999c1d9146757b2d7c961aefc453650f0651e032542f1bccce5fc4d5652c7f574145f159d2f19323bf8c6cbff' \
--header 'Content-Type: application/json' \
--data-raw '{ 
    "playerId" : "Cartman",
    "course" : 2,
    "score" : 354321
}'
```

## Beanstalk setup
* Create beanstalk app
  * give it a name
  * java
  * coretto 11
  * load local jar
  * create application
  * wait a while
* create rds postgres
  * https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/using-features.managing.db.html
  * I picked postgres and all the defaults
* ssl
  *