# XMchallenge
### Description
It’s time for XM developers to invest their salaries on cryptos. The problem is that we have no
idea of cryptos, so we are feeling a little bit afraid which crypto to choose. But is this actually a
problem? Of course not! We are developers and we always implement solutions for all the
problems we face.
For this one, we decided to build a recommendation service.

## Prerequisites
* Docker & docker-compose

### How to install and run the application
* clone the repo to your machine
* navigate into the directory: 
```
cd XMchallenge/
```
* run docker compose for launching the application:
```
docker-compose up
```
* in case you want to close the application run:
```
docker-compose down
```

### How to access the Swagger UI
After launching the application visit http://localhost:6868/swagger-ui/index.html
You can check the available APIs and test them via swagger interface

### How to access the database
After launching the application use the below command to connect to mysql database:
```
docker-compose exec mysqldb mysql -u $MYSQLDB_USER --password=$MYSQLDB_ROOT_PASSWORD
```
(username and password can be found in .env file). You can then use mysql CLI. 

### How to check the healthcheck of the application
After launching the application visit http://localhost:6868/actuator/health
For more details you can also check http://localhost:6868/actuator/health/custom

### How to run the Tests of the application
You can run the tests either by triggering manual the [Tests workflow](https://github.com/kevag4/XMchallenge/actions/workflows/tests.yml) and check the Reports, or locally. To try it locally, make sure you have first launched mysql database as a container and then run the tests:
```
docker-compose -f docker-compose-mysql.yml up
mvn test
```