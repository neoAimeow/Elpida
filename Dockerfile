FROM openjdk:8-jdk-alpine
MAINTAINER aimeow  a18814888787@gmail.com

VOLUME /tmp
ADD ./Elpida-0.0.1-SNAPSHOT.war app.war
EXPOSE 8080
ENTRYPOINT ["java" ,"-Djava.security.egd=file:/dev/./urandom" , "-jar","/app.war" , "--server.port=8080"]