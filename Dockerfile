FROM eclipse-temurin:21-jre-alpine
EXPOSE 5500
ADD target/Diplom-0.0.1-SNAPSHOT.jar myapp.jar
ENTRYPOINT ["java","-jar","/myapp.jar"]