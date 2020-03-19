# SEC Project 2019/20 - HDS DPAS System 

Group 16 - Alameda

Student Number | Name
------------- | -------------
83559 | Rodrigo Lima
86454 | João Martinho
94126 | Henrique Sousa

## Requirements
In order to build and run this project you will need:
* Apache-maven (at least 3.6.0)
* Java 1.8

## Compiling and running
In order to compile and run the whole project execute the following commands on **root folder** to install all dependencies:

    mvn clean install
    
Regarding the server, execute:

    cd announcementServer-ws
    mvn exec:java
    
Regarding the clients, execute:

    cd announcementServer-ws-cli
    mvn exec:java -Dexec.args="1"
    
where 1 is the ID of the client

