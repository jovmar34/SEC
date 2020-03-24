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
    mvn exec:java
    

## Paswords for KeyStore

User | Password | Hash (SHA 256)
-------- | -------- | --------
client1 | 25HtkQDXEQRT | 9bd915291749076d56d4198b4ea35003249be5c88acebce51fcf559d52bde24e
client2 | 5n2y5xcvkU86 | 4416f05dcc94e63edddd1e7459caefc6eb3137932ea64d446a08b2301aaefac6
client3 | SqPxGv9bKpqp | 27d728e7c5ed0f593fce0b49518a9d470826cac65778c5b5d2e14e2302db7636
server  | admin        | 8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
