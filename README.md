# SEC Project 2019/20 - HDS DPAS System 

Highly Dependable Systems Project - Dependable Public Announcement Server

Group 16 - Alameda

Student Number | Name
------------- | -------------
83559 | Rodrigo Lima
86454 | João Martinho
94126 | Henrique Sousa

## Requirements
In order to build and run this project you will need:
* GNU/Linux
* Apache-Maven 3.x.x 
* Java Development Kit 8

## Compiling
In order to compile the whole project execute the following command on **/announcementServer/** to install all dependencies:

    mvn clean install -DskipTests
    
**Note:** use -DskipTests in order to skip all the integration tests 

## Running
In order to run the system..
    
Regarding the server, execute:

    cd announcementServer-ws
    mvn exec:java
    
Regarding the clients, execute:

    cd announcementServer-ws-cli
    mvn exec:java -Dws.i=1
    
where 1 is the ID of the client

## Paswords for KeyStore

User | Password
-------- | --------
client1 | 25HtkQDXEQRT
client2 | 5n2y5xcvkU86
client3 | SqPxGv9bKpqp
client4 | BJfp4sKXyvfP
client5 | ZgLB2kBg8a42
server  | admin
