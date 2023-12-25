FROM openjdk:17

WORKDIR /app

RUN mkdir files

EXPOSE 8080

ADD target/AixmAndPostgis.jar AixmAndPostgis.jar

ENTRYPOINT ["java", "-jar", "AixmAndPostgis.jar"]