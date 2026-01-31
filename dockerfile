FROM eclipse-temurin:17-jdk

EXPOSE 8083

ADD back/target/Project_Fed-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
