[![CircleCI](https://circleci.com/gh/kcm3394/points-application.svg?style=svg)](https://circleci.com/gh/kcm3394/points-application)
# Points Application

REST API for storing and updating a user's points.

## Requirements

For building and running the application you need:

- [JDK 11](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [MySQL 8](https://dev.mysql.com/downloads/mysql/)

To test that you have the correct JDK installed, run the following commands:

```shell
$ javac -version
$ java -version
```

## Running the application locally

##### Setting up MySQL

To access the MySQL client on MacOS, enter the command:
```shell
$ /usr/local/mysql/bin/mysql -u root -p
```

This will prompt you to enter the password for the root user that you set up during installation.

From there, execute the following commands to initialize the database and user provided for this application.

```shell
mysql> create database if not exists pointsdb;
mysql> create user if not exists `points_user`@`localhost` identified with mysql_native_password by 'password';
mysql> grant all on pointsdb.* to 'points_user'@'localhost';
```

Please note that granting all access to `points_user` is not a good security practice in production code. You should 
then revoke all privileges from the MySQL user connected to the application and give only `SELECT`, `UPDATE`, `INSERT`, AND `DELETE`.

##### Running the application with IDE

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the 
`personal.kcm3394.points.PointsApplication` class from your IDE of choice.

##### Running the application with Maven

Alternatively, you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/maven-plugin/reference/htmlsingle/) 
from the folder containing the `pom.xml`:

```shell
$ ./mvnw spring-boot:run
```

##### Running the application with Executable JAR

The code can also be built into a jar and then executed/run. Once the jar is built, run the jar by double clicking on it or by using these 
commands from the folder containing the `pom.xml`:

```shell
$ ./mvnw clean package -DskipTests
$ java -jar target/points-0.0.1-SNAPSHOT.jar
```

To shutdown the jar on a MacOS, press `Ctrl` + `C`.

### Testing with Maven

Run tests using this command:

```shell
$ ./mvnw clean test
```

## Endpoints

Base url for all endpoints when running locally is `http://localhost:8080`

Method	| Path	| Description	|
------- | --------------------- | --------------------------------------------------------------------- |
GET	    | /points	            | Get point balance per user that lists all positive points per payer   |
POST	| /points/add-points	| Add points to user account for specific payer and date	            |
POST    | /points/spend-points	| Deduct points from user account and return a list of payers and points deducted per payer for each call |

##### Get point balance

Calls to `/points` will return a list of balances per payer. If no points have been added, a call to this endpoint will return an empty list.

```shell
$ curl http://localhost:8080/points
```

```json
[
    {
        "payerName": "CABBAGE CORP",
        "pointsBalance": 200
    },
    {
        "payerName": "ICE INDUSTRY",
        "pointsBalance": 500
    } 
]
```

##### Add points

Calls to `/points/add-points` require JSON data that includes `payerName`, `points`, and `transactionDate`.

```shell
$ curl -H 'Content-Type: application/json' -d '{"payerName":"CABBAGE CORP","points":200,"transactionDate":"2021-01-20T20:47:02"}' http://localhost:8080/points/add-points
```
##### Spend points

Calls to `/points/spend-points` require an integer parameter and will return a list of negated transactions.

```shell
$ curl -d "points=100" http://localhost:8080/points/spend-points
```

```json
{
    "payerName": "CABBAGE CORP",
    "points": -100,
    "transactionDate": "2021-01-19T11:57:53.353054"
}
```  

## Contributors

Kelsey Maka - [@kcm3394](https://github.com/kcm3394)