# Digital Banking Solution

This document contains diagrams and component definitions explaining the program architecture.


### Message routing

All incoming messages are processed by Apache Camel.

![messageRouting1.png](https://bitbucket.org/repo/ALkroe/images/308647501-messageRouting1.png)

The messages are unmarshalled according to the value of MessageType field of the received json object. Trading message is the wrapped inside ProcessingTask, which in turn is wrapped in SchedulingTask.

![messageWrapping.png](https://bitbucket.org/repo/ALkroe/images/3537114086-messageWrapping.png)

SchedulingTask represents a unit of work associated with routing the message to the ProductExchange of the specific product it refers to.

![messageRouting2.png](https://bitbucket.org/repo/ALkroe/images/3163014348-messageRouting2.png)

### Processing Task Scheduling

![orderSchedulingTask.png](https://bitbucket.org/repo/ALkroe/images/53221301-orderSchedulingTask.png)

SchedulingTasks saves the product name of the handled Order in IdProductIndex, so that Cancel and Modification SchedulingTasks know in which ProductExchange it can be found.

![cancelSchedulingTask.png](https://bitbucket.org/repo/ALkroe/images/874750033-cancelSchedulingTask.png)

![modificationSchedulingTask.png](https://bitbucket.org/repo/ALkroe/images/2878997432-modificationSchedulingTask.png)

### Handling Cancels 

An Order is cancelled by setting a Order.scheduledForDeletion flag to true, so that is is ignored when Orders are fetched from BUY/SELL queues.

### Handling Modifications

A copy of Order is made with new Details value and it is resubmitted like a newly incoming Order. Old Order is cancelled.

### Shutdown Notification Handling

When a Shutdown Notifications are received from all queues the following procedure begins:

1. Camel context is stopped, which means shutting down all executor services and waiting for the currently active Camel threads to terminate
2. All single threaded executors associated with ProductExchanges are shutdown.
3. All remaining ProcessingTasks from buffers are executed.

### Testing

All classes have been unit tested, to run them use the following command

```sh
$ mvn test -P unit
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
$ mvn clean cobertura:cobertura -P unit
```