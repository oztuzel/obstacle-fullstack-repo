FROM openjdk:17

EXPOSE 8080

ADD target/AixmAndPostgis.jar AixmAndPostgis.jar

#COPY munferit_2023_11_10_11_38.xlsx /munferit_2023_11_10_11_38.xlsx
#COPY LT_ENR_5_4_Obstacles_AIXM_5_1.xml /LT_ENR_5_4_Obstacles_AIXM_5_1.xml

ENTRYPOINT ["java", "-jar", "/AixmAndPostgis.jar"]