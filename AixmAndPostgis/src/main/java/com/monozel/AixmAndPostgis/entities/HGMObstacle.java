package com.monozel.AixmAndPostgis.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class HGMObstacle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String obstacleName;
    private String structureType;
    @Column(columnDefinition = "Geometry")
    private Point point;
    private double height;

    private String lighting;

    private double elevation;

    private double latitudeDMS;

    private double longitudeDMS;

    public HGMObstacle(String obstacleName, String structureType, Point point,
                       double height, String lighting, double elevation, double latitudeDMS, double longitudeDMS) {
        this.obstacleName = obstacleName;
        this.structureType = structureType;
        this.point = point;
        this.height = height;
        this.lighting = lighting;
        this.elevation = elevation;
        this.latitudeDMS = latitudeDMS;
        this.longitudeDMS = longitudeDMS;
    }
}
