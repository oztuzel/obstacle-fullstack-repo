FROM openjdk:17

EXPOSE 8080

ADD target/AixmAndPostgis.jar AixmAndPostgis.jar

WORKDIR /Documents/Projects/Obstacles

ENTRYPOINT ["java", "-jar", "/AixmAndPostgis.jar"]