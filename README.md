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
* Apache-Maven Version 3
* Java Development Kit 8

## Compiling
In order to compile the whole project execute the following command on **/announcementServer/** to install all dependencies:

    mvn clean install
    
**Note:** append -DskipTests to the above command in order to skip all tests 

## Running
In order to run the system..
    
Regarding the server, execute:

    cd announcementServer-ws
    mvn exec:java -Dws.i=y -Dw.f=x
    
Regarding the clients, execute:

    cd announcementServer-ws-cli
    mvn exec:java -Dws.f=x
    
**Note1:** In -Dws.f=x, x is the number of faults to tolerate (e.g: 1, max: 4)

**Note2:** In -Dws.i=y, y is the serverId (e.g: 1, max:16)

## Testing Criptographic mechanisms
In order to test criptographic mechanisms, we made some JUnit tests.
Those tests can be found in folder test ...

## Testing Bizantine Servers
In order to test the dependability requirements for bizantine servers, the process is:

1st step - start up 4 servers (use -Dws.i with range [1,4] and -Dws.f=1)

2nd step - start 1 client (-Dws.f=1)

3rd step - perform some operation to make sure the system is working

4th step - make 1 of the servers crash silently by closing that server's window

5th step - try to perform some operation while that server is down

**Note:** This specific tests allows for 1 byzantine fault

## Paswords

User | Password
-------- | --------
client1 | 25HtkQDXEQRT
client2 | 5n2y5xcvkU86
client3 | SqPxGv9bKpqp
--------| ------------ 
server1  | VeeU4FnDDJWP
server2  | PFVfR8drJbLp
server3  | ugLnV4Z9RQyk
server4  | cMHcs6mnttSE
server5  | 3VPSkn4ygYeK
server6  | rHG2UymMTR8A
server7  | ME8MykmgHBQd
server8  | CL7xL3mUkkhn
server9  | 7nGDc3cFyd3L
server10 | pBrj5JsFBwWN
server11 | 2tKMkWSdMU34
server12 | T6zEJr9WyYne
server13 | eCdFA4Sgfu5P
server14 | qX7HfjR9WdL6
server15 | v7URF2eBW8Cc
server16 | nLkceMWjuf3M

**Note:** Each keystore uses the password of the party that is related to the keys kept
