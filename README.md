# Museum management system
Simple java application for managing artworks of a museum. Project for the Advanced Programming Techniques course held by professor Lorenzo Bettini, University of Florence. 

This is a really simplified version of a museum management system developed in Java 8 using MongoDB as database.</br>
The code was written leveraging the advanced programming techniques practices, like TDD, AssertJ, PIT, JaCoCo, Coveralls, Docker and GitHub Actions.

###### Continuous integration (by [GitHub Actions](https://github.com/features/actions)):
[![CI with Maven, Coveralls, PIT and SonarCloud in Linux](https://github.com/CosimoGiani/museum-manager/actions/workflows/maven.yml/badge.svg)](https://github.com/CosimoGiani/museum-manager/actions/workflows/maven.yml)

###### Code coverage (by [Coveralls](https://coveralls.io/)):
[![Coverage Status](https://coveralls.io/repos/github/CosimoGiani/museum-manager/badge.svg?branch=master)](https://coveralls.io/github/CosimoGiani/museum-manager?branch=master)

###### Code quality (by [SonarCloud](https://www.sonarsource.com/products/sonarcloud/)):
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)</br>
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=bugs)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=sqale_index)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=CosimoGiani_museum-manager&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=CosimoGiani_museum-manager)

## Requirements
To follow the instructions on how to build the project, there are some prerequisites that need to be installed on your machine:
- Java 8
- Maven
- Docker

## Build the project and run tests
To build the project is necessary first to generate the executable JAR by running from the project's root directory:
```
mvn -f museum/pom.xml -DskiptTest=true package
```
Then start the Docker container of the MongoDB server:
```
mvn -f museum/pom.xml docker:start
```
Finally, run the application:
```
java -jar museum/target/museum-1.0.0-jar-with-dependencies.jar
```

When starting the application, it is also possible to specify the following additional arguments:
| Argument | Description |
| -- | -- |
| --mongo-host | MongoDB server address |
| --mongo-port | MongoDB server port |
| --db-name | Name of the database to use |
| --collection-artists-name | Name of the collection of artist in the database |
| --collection-works-name | Name of the collection of works in the database |

If none of the above parameter is specified, the application will be execu-ted using their default value.

### Tests
To run the tests locally, execute the following command from the project’s root directory:
```
mvn -f museum/pom.xml clean verify -Pjacoco,pit
```
where the jacoco and pit profiles are optional. When enabled they will execute respectively code coverage and mutation testing. By removing them the above command will run only the tests.
