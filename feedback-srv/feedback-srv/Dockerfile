FROM bellsoft/liberica-openjdk-alpine-musl
MAINTAINER feedback-srv
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} feedback-srv-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/feedback-srv-0.0.1-SNAPSHOT.jar"]
EXPOSE 11111