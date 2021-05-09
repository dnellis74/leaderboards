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