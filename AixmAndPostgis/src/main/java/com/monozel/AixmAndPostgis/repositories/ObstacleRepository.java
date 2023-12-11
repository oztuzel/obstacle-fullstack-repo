package com.monozel.AixmAndPostgis.repositories;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;
import com.monozel.AixmAndPostgis.entities.Obstacle;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ObstacleRepository extends JpaRepository<Obstacle, Long> {
    boolean existsByPoint(Point point);
    boolean existsByHeight(BigDecimal height);
    boolean existsByElevation(double elevation);
    boolean existsByType(String type);
    List<Obstacle> findByPoint(Point point);
    boolean existsByPointAndHeightAndElevationAndTypeAndDesignator(Point point, BigDecimal height, double elevation, String type, String designator);
    List<Obstacle> findByPointAndHeightAndElevationAndTypeAndDesignator(Point point, BigDecimal height, double elevation, String type, String designator);


    @Query(value = "SELECT Cast(json_build_object(" +
            " 'type', 'Feature' ," +
            " 'id',  id ," +
            " 'geometry', ST_AsGeoJSON(point) " +
            ") as text) FROM obstacle", nativeQuery = true)
    List<String> getGeoJsonFeatures();

    @Query(value = "SELECT CAST( json_build_object (" +
            "'type', 'FeatureCollection'," +
            " 'features', jsonb_agg ( feature ) " +
            ") " +
            "AS text)" +
            "FROM (" +
            "SELECT json_build_object (" +
            "'type', 'Feature', " +
            "'id' , id," +
            " 'geometry' , ST_AsGeoJSON(point) ," +
            " 'properties' , to_jsonb(row) - 'id' - 'point')" +         // id ve point columnlari haric table'daki diger rowlari aliyoruz.
            " AS feature " +
            "FROM ( SELECT * FROM obstacle) row ) features;" , nativeQuery = true)
    List<String> getFeatureCollectionInObstacle();

    @Query(value = "select id, st_astext(point) from obstacle" , nativeQuery = true)
    List<Object> getIdAndPoints();

    @Query(value = "SELECT st_asgeojson(point) from obstacle" , nativeQuery = true)
    List<String> getPointsGeoJson();

    // bu sorgu ile obstacle daki point ile hgmobstacle daki pointlerin yuzde yuz esit olmayanlarin obstacle'ini db'den cekiyoruz.
    @Query(value = "SELECT o.* AS obstacle_id From Obstacle o WHERE NOT EXISTS ( SELECT 1 FROM HGMObstacle h WHERE ST_Equals(o.point,h.point) );", nativeQuery = true)
    List<Obstacle> findSimilarPoints();


    // bu sorgu ile bir obstacle'in id sini verip o obstacle a en yakin 10 obstacle'in verilerini cekiyoruz.
    @Query(value = "SELECT o2.* " +
            "FROM " +
            "Obstacle o1, " +
            "Obstacle o2 " +
            "WHERE " +
            "o1.id = :obstacleId " +
            "AND NOT o1.designator = o2.designator " +
            "ORDER BY " +
            "ST_Distance(o1.point, o2.point) " +
            "LIMIT 10;", nativeQuery = true)
    List<Obstacle> findNearestObstacles(@Param("obstacleId") Long obstacleId);



    // bu query ile en yakin 10 obstacle'in designator'unu ve sectigimiz obstacle ile olan mesafesini cekiyoruz.
    @Query(value = "SELECT o2.designator," +
            "ST_distancesphere(o1.point,o2.point) " +   // bu method da sonuc direk metre olarak ciktigi icin bunu kullandim.
//            "ST_Distance(o1.point,o2.point) " +       // burasi radyan cinsinden veriyor ve metreye cevirirken cok dogru sonuc cikmiyor
            "FROM " +
            "Obstacle o1, " +
            "Obstacle o2 " +
            "WHERE " +
            "o1.id = :obstacleId " +
            "AND NOT o1.designator = o2.designator " +
            "ORDER BY " +
            "ST_Distance(o1.point, o2.point) " +
            "LIMIT 10;", nativeQuery = true)
    List<Object[]> findNearestObstaclesDistance(@Param("obstacleId") Long obstacleId);



    @Query(value = "SELECT o.id FROM Obstacle o WHERE ST_Distancesphere(o.point, :targetPoint) <= 30 AND o.id <> :targetObstacleId", nativeQuery = true)
    List<Long> findObstaclesWithin30Meters(@Param("targetPoint") Point targetPoint, @Param("targetObstacleId") Long targetObstacleId );

    @Query(value = "SELECT o.* FROM Obstacle o WHERE EXISTS (SELECT 1 FROM Obstacle o2 WHERE o2.id <> o.id AND ST_Distancesphere(o.point,o2.point) <= 30)", nativeQuery = true)
    List<Obstacle> findObstaclesWithin30M();



}
