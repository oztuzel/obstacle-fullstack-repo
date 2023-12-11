package com.monozel.AixmAndPostgis.entities;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class Obstacle {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String designator;

    private String aixm_name;

    @Column(name = "type_name")
    private String type ;

//    @Column(columnDefinition = "geometry(PointZ)")
    @Column(columnDefinition = "Geometry")
    private org.locationtech.jts.geom.Point point;  // this points have x,y and optional z,m

    private BigDecimal height;

    private String lighting;

    private String colour;

    private double elevation;

    private double latitudeDMS;

    private double longitudeDMS;

    @Column(name = "group_name")
    private String group;

    public Obstacle(String designator, String aixm_name,
                    String type, BigDecimal height,
                    String lighting, String colour, String group,
                    org.locationtech.jts.geom.Point point, double elevation,
                    double latitudeDMS, double longitudeDMS
                    ) {

        this.designator = designator;
        this.aixm_name = aixm_name;
        this.type = type;
        this.elevation = elevation;

        this.latitudeDMS = latitudeDMS;
        this.longitudeDMS = longitudeDMS;
        this.point = point;

        this.height = height;
        this.lighting = lighting;
        this.colour = colour;
        this.group = group;

    }


}
