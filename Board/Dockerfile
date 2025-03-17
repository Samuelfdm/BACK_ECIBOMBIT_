FROM openjdk:21

WORKDIR /usrapp/bin

EXPOSE 8082

COPY target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]