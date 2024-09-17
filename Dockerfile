FROM eclipse-temurin:21-jre

ADD target/question-service.jar question-service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","question-service.jar"]