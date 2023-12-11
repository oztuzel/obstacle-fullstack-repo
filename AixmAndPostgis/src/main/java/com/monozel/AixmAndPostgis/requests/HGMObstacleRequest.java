package com.monozel.AixmAndPostgis.requests;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class HGMObstacleRequest {

    private String obstacleName;
    private String structureType;
    private double height;
    private String lighting;

    private double latitude;
    private double longitude;
    private double elevation; // bunu xml'e cevirirken string e cevir !

    private double latitudeDMS;

    private double longitudeDMS;


    public HGMObstacleRequest(HGMObstacle hgmObstacle) {
        this.height = hgmObstacle.getHeight();
        this.lighting = hgmObstacle.getLighting();

        this.elevation = hgmObstacle.getElevation(); // feet
        this.latitude = hgmObstacle.getPoint().getCoordinate().getX(); // derece dakika saniye degil ham hali
        this.longitude = hgmObstacle.getPoint().getCoordinate().getY();
        this.longitudeDMS = hgmObstacle.getLongitudeDMS();
        this.latitudeDMS = hgmObstacle.getLatitudeDMS();

        this.obstacleName = hgmObstacle.getObstacleName();
        this.structureType = hgmObstacle.getStructureType();
    }



}
