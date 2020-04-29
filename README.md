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

    mvn clean install
    
**Note:** append -DskipTests to the above command in order to skip all tests 

## Running
In order to run the system..
    
Regarding the server, execute:

    cd announcementServer-ws -Dws.i=y -Dw.f=x
    mvn exec:java
    
Regarding the clients, execute:

    cd announcementServer-ws-cli
    mvn exec:java -Dws.f=x
    
**Note1:** In -Dws.f=x, x is the number of faults to tolerate (e.g: 1, max: 4)

**Note2:** In -Dws.i=y, y is the serverId (e.g: 1, max:16)

## Paswords for Users

User | Password | Hash (SHA 256)
-------- | -------- | --------
client1 | 25HtkQDXEQRT | 9bd915291749076d56d4198b4ea35003249be5c88acebce51fcf559d52bde24e
client2 | 5n2y5xcvkU86 | 4416f05dcc94e63edddd1e7459caefc6eb3137932ea64d446a08b2301aaefac6
client3 | SqPxGv9bKpqp | 27d728e7c5ed0f593fce0b49518a9d470826cac65778c5b5d2e14e2302db7636
--------| ------------ | ----------------
server1  | VeeU4FnDDJWP | dd3cb0d471b4f9814f5e77be672cb7125b6a4e034c9c4641501869eef2e7ef71
server2  | PFVfR8drJbLp | 1f9bbf7b8d4edafeab88a4c26d660579be7c81bd2a8354155a3977927b4d2b1f
server3  | ugLnV4Z9RQyk | ee28fc6873f8c2d91552aa14fab764e0e904ff6319cc8678d55b925a4de5b927
server4  | cMHcs6mnttSE | afb8d550fd557966c94a83bc652c4dad6c304fc22c7ca4fe08f316c5348bdf53
server5  | 3VPSkn4ygYeK | f2643cac960bd41340815ecf27b8ab4c162e48b5437d36b07949d6e5baa4b58f
server6  | rHG2UymMTR8A | 02b7c05f4c30fb9f223f5148383a540a6322a98c1d7f854c5005e63d92f7c387
server7  | ME8MykmgHBQd | 01627c2b0f17e44e52349115fecef0a3df99babb662a27d6bdee9d111795fbd6
server8  | CL7xL3mUkkhn | 661ac196bf4a45a49cbf77bb4e6fa0e4cf5c16c2e6e279aec221ec2ed3383574
server9  | 7nGDc3cFyd3L | bee96d98a0fc27e86056213815bbb03fac31e0e4536501640b9c4902526f7758
server10 | pBrj5JsFBwWN | e4688fae58d6b0293116085c8f0b43045e91c394c28946e5cb67f2789135152a
server11 | 2tKMkWSdMU34 | 38638ce4c002389438e3a98fd498fa9c5085ad83d895cfd352e0bb9953dd4881
server12 | T6zEJr9WyYne | 5ad64c1e541352c75ac84a4d6ec3f13fe6115d697270d830f25f164c6a44e762
server13 | eCdFA4Sgfu5P | 53cd1867f57f0ed3fb2fb2c49d95977f74c356379358b51eddf79784338034b5
server14 | qX7HfjR9WdL6 | af9cc0174520738e1aec6cafb69f92733cbdb635d5497345299d0ab872f367ee
server15 | v7URF2eBW8Cc | 806e8fbd2761ee771977a96632d39b285de78e4dbf87113cee26d23eac48c4cb
server16 | nLkceMWjuf3M | bf2969b26bb385526223d7b3fc35c690f056e5b3feede478df85ef88b2ab68e7

## Password for Keystore

Password |
---------|
K6nEsCKNTPjy7vKq|
