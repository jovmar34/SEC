cd announcementServer

cd announcementServer-ws-cli
start mvn exec:java

cd ..\announcementServer-ws
start mvn exec:java 
start mvn exec:java -Dws.i=2 
start mvn exec:java -Dws.i=3 
start mvn exec:java -Dws.i=4 