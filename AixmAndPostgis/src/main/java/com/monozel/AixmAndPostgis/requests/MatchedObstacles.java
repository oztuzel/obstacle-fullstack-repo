package com.monozel.AixmAndPostgis.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchedObstacles {
    private ObstacleRequest obstacleRequest;
    private List<HGMObstacleRequest> hgmObstacleRequestList;
}
