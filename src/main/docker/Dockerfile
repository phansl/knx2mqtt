FROM java:8 

MAINTAINER Philip Peterhansl (phansl) <philip@peterhansl.it>

EXPOSE 8080

USER root
RUN apt-get update && apt-get install -y iputils-ping telnet
ADD knx2mqtt-0.0.1-SNAPSHOT.jar /opt/knx2mqtt/

CMD java -Djava.security.egd=file:/dev/./urandom -jar /opt/knx2mqtt/knx2mqtt-0.0.1-SNAPSHOT.jar
