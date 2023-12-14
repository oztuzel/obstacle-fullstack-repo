package com.monozel.AixmAndPostgis.repositories;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HGMObstacleRepository extends JpaRepository<HGMObstacle, Long> {
    boolean existsByPoint(Point point);
    boolean existsByPointAndElevationAndHeightAndObstacleNameAndStructureTypeAndLighting(Point point, double elevation, double height, String obstacleName, String structureType, String lighting);

    List<HGMObstacle> findByPoint(Point point);


//    @Query(value = "SELECT o.id AS obstacle_id, h.id AS hgm_obstacle_id From Obstacle o, HGMObstacle h WHERE ST_Equals(o.point, h.point) = true", nativeQuery = true)
//    List<Object[]> findEqualsPoints();

    // bu sorgu ile obstacle point ile hgmobstacle point yuzde yuz eslesiyorsa o ikilileri [obstacleID, hgmobstacleID] seklinde aliyoruz.
    @Query(value = "SELECT o.id AS obstacle_id, h.id AS hgm_obstacle_id FROM Obstacle o JOIN HGMObstacle h ON ST_Equals(o.point, h.point) = true", nativeQuery = true)
    List<Long[]> findEqualsPoints();


    // bu sorgu ile obstacle daki point ile hgmobstacle daki pointlerin yuzde yuz esit olmayanlari hgmobstaclelari db'den aliyoruz.
    @Query(value = "SELECT h.* AS hgm_obstacle_id From HGMObstacle h WHERE NOT EXISTS ( SELECT 1 FROM Obstacle o WHERE ST_Equals(o.point,h.point) );", nativeQuery = true)
    List<HGMObstacle> findSimilarPoints();


    // bir nokta sectigimizde o noktaya gore en yakin 10 noktayi buluyoruz.
    @Query(value = "SELECT " +
            "h2.id, " +
            "h2.elevation , " +
            "h2.height, " +
            "h2.point, " +
//            "ST_Distance(h1.point, h2.point) AS distance " +
            "h2.latitudedms, " +
            "h2.lighting, " +
            "h2.obstacle_name, " +
            "h2.structure_type, " +
            "h2.longitudedms " +
            "FROM " +
            "HGMObstacle h1, " +
            "HGMObstacle h2 " +
            "WHERE " +
            "h1.id = :hgmObstacleId " +
            "AND h1.id != h2.id " +
            "ORDER BY " +
            "ST_Distance(h1.point, h2.point) " +
            "LIMIT 10;", nativeQuery = true)
    List<HGMObstacle> findNearestHGMObstacles(@Param("hgmObstacleId") Long hgmObstacleId);


}
