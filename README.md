# 🐢 Turtle Park

Turtle Park is a thread-safe implementation of a client/server service to handle a theme park. The user, via four different command-line applications, can interact with the server, performing actions such as loading attractions, adding visitors, booking rides, getting statistics, and receiving notifications.

All client applications and the server are written in Java. The communication between them is carried out with gRPC.

<hr>

* [1. Prerequisites](#1-prerequisites)
* [2. Compiling](#2-compiling)
* [3. Executing Turtle Park](#3-executing-turtle-park)
  * [3.1. Server](#31-server)
  * [3.2. Administration Client](#32-administration-client)
      * [3.2.1. Load a set of rides](#321-load-a-set-of-rides)
      * [3.2.2. Load a set of tickets](#322-load-a-set-of-tickets)
      * [3.2.3. Load ride capacity for a day](#323-load-ride-capacity-for-a-day)
  * [3.3. Booking Client](#33-booking-client)
      * [3.3.1. Print attraction details](#331-print-attraction-details)
      * [3.3.2. Get availability of one or more attractions](#332-get-availability-of-one-or-more-attractions)
      * [3.3.3. Book a ride](#333-book-a-ride)
      * [3.3.4. Confirm a booking](#334-confirm-a-booking)
      * [3.3.5. Cancel a booking](#335-cancel-a-booking)
  * [3.4. Notification Client](#34-notification-client)
      * [3.4.1. Register for notifications for a specific day and ride](#341-register-for-notifications-for-a-specific-day-and-ride)
      * [3.4.2. Unregister for notifications for a specific day and ride](#342-unregister-for-notifications-for-a-specific-day-and-ride)
  * [3.5. Query Client](#35-query-client)
      * [3.5.1. Get suggested capacity for all rides of the park](#351-get-suggested-capacity-for-all-rides-of-the-park)
      * [3.5.2. Get a list of confirmed bookings](#352-get-a-list-of-confirmed-bookings)
* [4. Final Remarks](#4-final-remarks)

<hr>

## 1. Prerequisites

The following prerequisites are needed to run the server executable as well as the client applications:

* Maven
* Java 19

## 2. Compiling

To compile the project and get all executables, `cd` into the root of the project, and run the following command:

```Bash
mvn clean package
```

This will create two `.tar.gz` files, that contain all of the files necessary to run the clients and the server respectively.  Their location is:
* **Client**: `./client/target/tpe1-g2-client-1.0-SNAPSHOT-bin.tar.gz`
* **Server**: `./server/target/tpe1-g2-server-1.0-SNAPSHOT-bin.tar.gz`

## 3. Executing Turtle Park

Unpack both the server and the client using:

```Bash
tar -xf <file.tar.gz>
```

> ⚠️ From now on, it is assumed that the server files are located inside the `./server` directory and the client files are located inside the `./client` directory.

Then, give all executables the needed permissions to be executed:

```Bash
chmod u+x ./client/*-cli ./server/turtle-park
```

### 3.1. Server

Must be running for the clients to work. Once it's stopped, all stored data is flushed.

> 🚨 The current working directory **must** be `./server`.

```
./turtle-park [ -Dport=XXXX ]
```

* The default port for the server is `7321`, it can be overriden as shown above.

### 3.2. Administration Client

> 🚨 The current working directory **must** be `./client`.

Three operations are supported:

#### 3.2.1. Load a set of rides

```Bash
./admin-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=rides  -DinPath=./path.csv 
```

The path must point to a Comma Separated Values file, using `;` as a delimiter. The file **must** have the following fields:

* Name (String)
* Opening time (HH:MM)
* Closing time (HH:MM)
* Slot size, in minutes (positive integer)

See below an example:

```Text
name;hoursFrom;hoursTo;slotGap
SpaceMountain;09:00;22:00;30
TronLightcycle;10:00;22:00;15
```

#### 3.2.2. Load a set of tickets

```Bash
./admin-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=tickets  -DinPath=./path.csv 
```

The path must point to a Comma Separated Values file, using `;` as a delimiter. The file **must** have the following fields:

* Visitor ID (UUID)
* Ticket type (UNLIMITED | THREE | HALF_DAY)
* Day of year where the ticket is valid (positive integer)

See below an example:

```Text
visitorId;passType;dayOfYear
ca286ef0-162a-42fd-b9ea-60166ff0a593;UNLIMITED;100
2af16ea7-4af1-47f6-bf46-8515de5a500f;HALF_DAY;15
```

#### 3.2.3. Load ride capacity for a day

```Bash
./admin-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=slots -Dday=XXX -Dride=XXXXXXXX -Dcapacity=XXX
```

### 3.3. Booking Client

> 🚨 The current working directory **must** be `./client`.

Five operations are supported:

#### 3.3.1. Print attraction details

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=attractions
```

#### 3.3.2. Get availability of one or more attractions

You can get the availability for a specific day, slot and ride by:

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=availability -Dday=XXX -Dride=XXXXXXXX -Dslot=XX:XX
```

You can also get the availability for a range of slots for the same attraction by:

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=availability -Dday=XXX -Dride=XXXXXXXX -Dslot=XX:XX -DslotTo=XX:XX
```

Lastly, you can also get the availability for a range of slots for all of the attractions in the park by:

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=availability -Dday=XXX -Dslot=XX:XX -DslotTo=XX:XX
```

#### 3.3.3. Book a ride

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=book -Dday=XXX -Dvisitor=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX -Dride=XXXXXXXX -Dslot=15:30
```

#### 3.3.4. Confirm a booking

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=confirm -Dday=XXX -Dvisitor=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX -Dride=XXXXXXXX -Dslot=15:30
```

#### 3.3.5. Cancel a booking

```Bash
./book-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=cancel -Dday=XXX -Dvisitor=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX -Dride=XXXXXXXX -Dslot=15:30
```

### 3.4. Notification Client

> 🚨 The current working directory **must** be `./client`.

Two operations are supported:

#### 3.4.1. Register for notifications for a specific day and ride

```Bash
./notif-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=follow -Dday=XXX -Dvisitor=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX -Dride=XXXXXXXX
```

The terminal where this command is running will be running until one of the following things occur:

* All of the bookings for the visitor are confirmed or cancelled
* The server receives an `unfollow` request for the visitor
* The user kills the process

#### 3.4.2. Unregister for notifications for a specific day and ride

```Bash
./notif-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=unfollow -Dday=XXX -Dvisitor=XXXXXXXX-XXXX-XXXX-XXXX-XXXXXXXXXXXX -Dride=XXXXXXXX
```

### 3.5. Query Client

> 🚨 The current working directory **must** be `./client`.

Two operations are supported:

#### 3.5.1. Get suggested capacity for all rides of the park

```Bash
./query-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=capacity -Dday=XXX -DoutPath=./path.txt 
```

The path must point to a file that will be created or overriden, with the query results. The file **will** have the following fields in a table:

* Slot (HH:MM)
* Suggested capacity (positive integer)
* Attraction (String)

See below an example:

```Text
Slot  | Capacity | Attraction
13:00 |       27 | Tron Lightcycle
12:45 |       23 | Space Mountain
09:00 |        0 | The Hall of Presidents
```

#### 3.5.2. Get a list of confirmed bookings

```Bash
./query-cli -DserverAddress=XX.XX.XX.XX:YYYY -Daction=confirmed -Dday=XXX -DoutPath=./path.txt 
```

The path must point to a file that will be created or overriden, with the query results. The file **will** have the following fields in a table:

* Slot (HH:MM)
* Visitor ID (UUID)
* Attraction (String)

See below an example:

```Text
Slot  | Visitor                              | Attraction
15:30 | ca286ef0-162a-42fd-b9ea-60166ff0a593 | Space Mountain
13:00 | ca286ef0-162a-42fd-b9ea-60166ff0a593 | Tron Lightcycle
13:00 | 2af16ea7-4af1-47f6-bf46-8515de5a500f | Tron Lightcycle
```

## 4. Final Remarks

This project was done in an academic environment, as part of the curriculum of Distributed Objects Programming from Instituto Tecnológico de Buenos Aires (ITBA)

The project was carried out by:

* Alejo Flores Lucey
* Andrés Carro Wetzel
* Nehuén Gabriel Llanos
