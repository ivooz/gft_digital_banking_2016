# Digital Banking Solution

This document contains diagrams and component definitions explaining the program architecture.


### Message routing

All incoming messages are processed by Apache Camel.
![Camel phase 1](https://bytebucket.org/gftcontest2016team35/contest2016team35/raw/master/images/messageRouting1.png)
The messages are unmarshalled according to the value of MessageType field of the received json object. Trading message is the wrapped inside ProcessingTask, which in turn is wrapped in SchedulingTask.
![Wrapping](https://bytebucket.org/gftcontest2016team35/contest2016team35/raw/master/images/messageWrapping.png)
SchedulingTask represents a unit of work associated with routing the message to the ProductExchange of the specific product it refers to.
![Camel phase 2](https://bytebucket.org/gftcontest2016team35/contest2016team35/raw/master/images/messageRouting.png)


### Processing Task Scheduling

### Processing Task Execution

### Testing

All classes have been unit tested, to run them use the following command

```sh
$ mvn test -P unit-tests
```

For performance tests run:

```sh
$ mvn test -P performance
```

For functional verification run:

```sh
$ mvn test -P functional
```

To generate unit-test code coverage report run:

```sh
$ mvn clean cobertura:cobertura -P unit-tests
```
