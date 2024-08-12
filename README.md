# Voltmasters: Transaction Server
[![Transaction CI](https://github.com/voltmasters/transaction/actions/workflows/authorization-ci.yml/badge.svg)](https://github.com/voltmasters/transaction/actions/workflows/authorization-ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=voltmasters_authorization&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=voltmasters_authorization)

<!-- Image from etc -->
![Voltmasters](etc/charging.png)

## Description
This is the **transaction** server for the Voltmasters project.
It is responsible for managing the authentication and authorization of users.
It is built using the Spring Boot framework.
Uses Apache Kafka for messaging.
Accepts http requests and sends responses to the Kafka cluster.

## Built With
- Spring Boot
- Apache Kafka

## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites for Running
- Docker Compose

Check if Docker Compose is installed by running this command in the terminal or command prompt
```shell
docker compose version
```

_Note: If you already have the Kafka cluster running, you can skip this step._

Now just run this command to start the application inside the project directory
```shell
docker compose -f kafka-cluster.yml -f docker-compose.yml up -d
```

If you already have a Kafka Cluster running please edit the `docker-compose.yml` file.
And edit the `SPRING_KAFKA_BOOTSTRAP_SERVERS` environment variable to point to the broker(s).
Then run this command to start the application inside the project directory
```shell
docker compose up -d
```

To stop the application, run this command in the project directory
```shell
docker compose down
```

### Prerequisites for Development
- Java 21
- Maven 3
- Docker
- IDE (IntelliJ IDEA, Eclipse, etc.)
- Git

### Running the Application
1. Clone the repository using Git in the terminal
```shell
git clone git@github.com:voltmasters/transaction.git
```
2. Open the directory in the terminal
```shell
cd transaction
```
3. Run the application using Docker Compose along with the Kafka Cluster
```shell
docker compose -f kafka-cluster.yml -f docker-compose.yml up -d
```
If you don't want to run the Kafka Cluster, you can run the application using the following command
```shell
docker compose up -d
```


#### Kafka Cluster
- The kafka Cluster will be available at `localhost:9092,localhost:9093,localhost:9094`
- The docker internal network will be available at `kafka1:29092,kafka2:29093,kafka3:29094`
- You can also visualize the kafka cluster using [kafka-ui](https://docs.kafka-ui.provectus.io/) at [localhost:8080](http://localhost:8080/)

### Running the Tests
1. Run the tests using Maven in the project directory
```shell
mvn test
```

### Accessing the Application
- The application will be available at `localhost:8089`
- You can access the application using the following URL
```shell
http://localhost:8089
```
- The application has a single endpoint `/transaction/authorize` which accepts `POST` requests.
- As the application is posting to the topic `charge-authorization-request`.
- You can use Postman or Insomnia to send requests.
```shell
curl --request POST \
  --url http://localhost:8088/transaction/authorize \
  --header 'Content-Type: application/json' \
  --data '{
    "stationUuid": "257a19ed-4bff-4e32-b692-7da2e3860efe",
    "driverIdentifier": {
      "id": "257a19ed-4bff-4e32-b692-7da2e3860ef2"
    }
  }
'
```
or
```shell
wget --quiet \
  --method POST \
  --header 'Content-Type: application/json' \
  --body-data '{\n	"stationUuid": "257a19ed-4bff-4e32-b692-7da2e3860efe",\n	"driverIdentifier": {\n		"id": "257a19ed-4bff-4e32-b692-7da2e3860ef2"\n	}\n}\n' \
  --output-document \
  - http://localhost:8088/transaction/authorize
```
- The application will listen to the topic and respond to the requests.
- The application will listen to the topic `charge-authorization-response`.
- You can check the logs to see the responses.

#### Example Request
```json
{
  "stationUuid": "257a19ed-4bff-4e32-b692-7da2e3860ef3",
  "driverIdentifier": {
    "id": "257a19ed-4bff-4e32-b692-7da2e3860ef3"
  }
}
```

##### Parameters Description
- `stationUuid`: `UUIDv4` as `string` The unique identifier for the station.
- `driverIdentifier`: `JSON Object` The object containing the driver's identifier.
  - `id`: `string` The unique identifier for the driver.
    - the driver identifier needs to be between 20 and 80 characters long.

#### Example Response
```json
{
  "authorizationStatus": "Accepted"
}
```

##### Parameters Description
- `authorizationStatus`: `string` The status of the authorization request.
    - There are **four** possible values:
        - `Accepted`: identifier is known and a flag is set which says the card is allowed to charge.
        - `Rejected`: identifier is known but card is not allowed for charging.
        - `Unknown`: identifier is not known.
        - `Invalid`: identifier is not valid.

### Improvments
- [ ] Implement distributed tracing
- [ ] Add more tests and increase test coverage
- [ ] Add more documentation to help understand the internal workings of the application
- [ ] Add robust error handling and logging
- [ ] Add security features
- [ ] *Please suggest more improvements to [contact@subhrodip.com](mailto:contact@subhrodip.com)
 or simple raise an [issue](https://github.com/voltmasters/transaction/issues/new/choose)*
