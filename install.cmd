cd announcementServer\announcementServer-common
start mvn clean install -DskipTests
timeout 20
cd ..\announcementServer-ws
start mvn clean compile
cd ..\announcementServer-ws-cli
start mvn clean compile
