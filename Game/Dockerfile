FROM openjdk:21

WORKDIR /usrapp/bin

EXPOSE 8080

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]