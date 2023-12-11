package com.monozel.AixmAndPostgis.requests;

import com.monozel.AixmAndPostgis.entities.Obstacle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObstacleRequest {

    private String designator;

    private String aixm_name;

    private String type;

//    private org.locationtech.jts.geom.Point point;  // this points have x,y and optional z,m (z = xml deki elevation)
    private double latitude;
    private double longitude;
    private double elevation; // bunu xml'e cevirirken string e cevir !

    private BigDecimal height;

    private String lighting;

    private String colour;

    private String group;

    private double latitudeDMS;

    private double longitudeDMS;


    public ObstacleRequest(Obstacle obstacle) {
        this.height = obstacle.getHeight();
        this.colour = obstacle.getColour();
        this.group = obstacle.getGroup();
        this.designator = obstacle.getDesignator();
        this.lighting = obstacle.getLighting();
        this.aixm_name = obstacle.getAixm_name();
        this.type = obstacle.getType();

        this.elevation = obstacle.getElevation();

        this.latitudeDMS = obstacle.getLatitudeDMS();
        this.longitudeDMS = obstacle.getLongitudeDMS();

        this.latitude = obstacle.getPoint().getCoordinate().getX();
        this.longitude = obstacle.getPoint().getCoordinate().getY();
    }


}
