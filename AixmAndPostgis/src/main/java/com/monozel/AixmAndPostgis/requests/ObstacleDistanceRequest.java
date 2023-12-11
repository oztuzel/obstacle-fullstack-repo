package com.monozel.AixmAndPostgis.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObstacleDistanceRequest {

    private ObstacleRequest obstacleRequest;
    private double distance;
}
