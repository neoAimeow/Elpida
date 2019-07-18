FROM openjdk:8-jdk-alpine
MAINTAINER aimeow  a18814888787@gmail.com

VOLUME /tmp
ADD ./Elpida-api-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java" ,"-Djava.security.egd=file:/dev/./urandom" , "-jar","/app.jar" , "--server.port=8080"]