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
* You can run the tests either by triggering manual the [Tests workflow](https://github.com/kevag4/XMchallenge/actions/workflows/tests.yml) and check the Reports. (<ins>This is not working 100% as 3 cases are failing but did not have time to troubleshoot it. Probably something to do with timezones and the way the entries are fetched due to their timestamp</ins>).  
* You can also run tests locally, just make sure you have first launched mysql database as a container and then run the tests:
    ```
    docker-compose -f docker-compose-mysql.yml up
    mvn test
    ```

### Rate Limiting
The application allows 60 requests per minute per IP. This is configurable through the applications properties



### Notes from the developer

In general I am quite happy of what I achieved as I could not invest much time in this assessment. I tried to follow all your instructions and do it as simpler as I could, again for avoiding spending much time on this.

#### Tech stack
I have used spring boot with JPA and java 11 because it is a very handy and quick way to create a REST API microservice. I have created just HTTP APIS, although I am also fond of gRPC and Protocol Buffers.
Regarding the database approach, I used mysql with docker-compose in order to have it as a seperate  microservice. This is of course better approach. I have chosen SQL db because I personally am more familiar and it would be quicker for me to implement this excersice. 
I also used the acuator for checking the healthcheck and Swagger for having an easy and pretty way of UI API documention.
Finally, for the rate limiting I have used bucket4j spring boot starter together with ehcache

#### Testing
I used JUnit 5 and MockMVC and mostly focused on integration tests, rather that testing each layer seperately. I mostly did that for time sake as I could not afford more time. I am partially happy from the test coverage, I should have made a more solid test plan, but I did a fairly good work in the time given I believe...

#### Assumptions
* I have created an abstract base Crypto class with cryto "symbol" as JsonTypeInfo. You will also see all JsonSubTypes supported in order to create safeguard for NOT supported cryptos. Then each crypto extends the base Crypto class easily and this way we can add more cryptos in the future. Note that I used a single table for all cryptos. 
* On startup the provided csvs are parsed and the db is populated. Then all data retrieval that you asked is possible through sql queries. I believe I am performing the correct calculations in the normalization requirement and all APIs should return correct data. Sorry if I have not understood well the exact calculations that were asked in the exercise.
* You will see I am using Paging for the APIs that are returning a lot of entries. I did that in order the user to be able to better handle responses with large datasets
* Error handling is NOT performed. Just some basic stuff have been implemented, but this is something that needs to added for sure in an app like this, I just did not have the time to do it properly
* I have NOT used Asynchronous APIs at all. 

#### Extensions or improvements to the service
* Proper authentication/authorization mechanism. This can also achieved with separate ingress peer microservice
* Proper handling of db secrets and environmental variables (e.g use github secrets).
* Use of profiles inside docker compose for different environments
* Better testing coverage
* Proper error handling
* Performance/robustness testing. (Test suite that can perform daily or weekly runs, for both functional and performance tests is a good idea)

## Enjoy!!