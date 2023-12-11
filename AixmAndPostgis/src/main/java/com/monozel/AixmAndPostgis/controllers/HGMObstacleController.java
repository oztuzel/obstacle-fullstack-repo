package com.monozel.AixmAndPostgis.controllers;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;
import com.monozel.AixmAndPostgis.requests.HGMObstacleRequest;
import com.monozel.AixmAndPostgis.services.HGMObstacleService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/HGMObstacles")
public class HGMObstacleController {

    private HGMObstacleService hgmObstacleService;

    // id si elimizde olan hgmObstacle'i db'den cekme
    @GetMapping("/{id}")
    public HGMObstacleRequest getHGMObstacleById(@PathVariable Long id) {
        return hgmObstacleService.findById(id);
    }

    // React App'imizden gelen excel dosyasi var ve icinden hgm nin excel verilerini okuyup HGMObstacle olusturup database e kaydediyoruz.
    // Ama List<HGMObstacle> listesi donduremiyoruz cunku Point kendi icinde bir dongu olusturuyor bu sebeple List<HGMObstacleRequest> donduruyoruz.
    @PostMapping("/readHGMExcel")
    public List<HGMObstacleRequest> readExcelFile(@RequestParam("file") MultipartFile file){
        List<HGMObstacle> hgmObstacleList = hgmObstacleService.readExcel(file);

        List<HGMObstacleRequest> hgmObstacleRequestList = new ArrayList<>();
        for(HGMObstacle obstacle: hgmObstacleList) {
            hgmObstacleRequestList.add(new HGMObstacleRequest(obstacle));
        }
        return hgmObstacleRequestList;

    }

    @GetMapping("/findEquals")
    public List<Object[]> findEqualsPoints(){
        System.out.println(hgmObstacleService.findEqualsPoints().size());
        return hgmObstacleService.findEqualsPoints();
    }

    @GetMapping("/findSimilarPoints")
    public List<HGMObstacleRequest> findSimilarPoints(){
        System.out.println(hgmObstacleService.findSimilarPoints().size());
        return hgmObstacleService.findSimilarPoints();
    }

    @GetMapping("/nearestPoints")
    public  List<HGMObstacleRequest> findNearestTenHGMObstacles(@RequestParam("hgmObstacleId") Long hgmObstacleID){
        return hgmObstacleService.findNearestTenPoints(hgmObstacleID);
    }

}
