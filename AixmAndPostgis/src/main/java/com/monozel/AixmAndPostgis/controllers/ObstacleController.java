package com.monozel.AixmAndPostgis.controllers;

import aero.aixm.message.AIXMBasicMessageType;
import com.monozel.AixmAndPostgis.entities.Obstacle;
import com.monozel.AixmAndPostgis.requests.ObstacleDistanceRequest;
import com.monozel.AixmAndPostgis.requests.ObstacleRequest;
import com.monozel.AixmAndPostgis.services.JavaXmlConvertService;
import com.monozel.AixmAndPostgis.services.ObstacleService;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/obstacles")
public class ObstacleController {

    private ObstacleService obstacleService;
    private JavaXmlConvertService javaXmlConvertService;

    // Obstacle'lari database'den alip ObstacleRequest'e cevirip liste seklinde donduruyoruz.
    @GetMapping
    public List<ObstacleRequest> getAllObstacles () {
        List<Obstacle> obstacleList = obstacleService.getAllObstacles();
        List<ObstacleRequest> obstacleRequestList = new ArrayList<>();
        for(Obstacle obstacle: obstacleList) {
            obstacleRequestList.add(new ObstacleRequest(obstacle));
        }
        return obstacleRequestList;

    }

    @GetMapping("/{id}")
    public ObstacleRequest getHGMObstacleById(@PathVariable Long id) {
        return obstacleService.findById(id);
    }

    @GetMapping("/findSimilarPoints")
    public Map<Long, List<Long>> findSimilarObstacles () {
        return obstacleService.findSimilarPoints();

    }

    @GetMapping("/findPointsSameIntegerCoordinates")
    public Map<Long, List<Long>> findOtherSimilarObstacles() {
        System.out.println(obstacleService.findSamePointsWithInteger().size());
        return obstacleService.findSamePointsWithInteger();
    }

    @GetMapping("/nearestPoints")
    public  List<ObstacleDistanceRequest> findNearestTenHGMObstacles(@RequestParam("obstacleId") Long obstacleID){
        return obstacleService.findNearestTenPoints(obstacleID);
    }


    @GetMapping("/findObstaclesWithin30MetersForEach")
    public Map<Long,List<Long>> findObstaclesWithin30MetersForEach () {
        return obstacleService.findObstaclesWithin30MetersForEach();
    }

    @GetMapping("/findObstaclesWith30")
    public List<ObstacleRequest> findObstaclesWith30 () {
        return obstacleService.findObstaclesWith30();
    }

    // react ile bir 5.1 aixm veri degisim modeline uygun xml dosyasini okutup buraya post mapping ile atiyoruz ve onu yakaliyoruz.
    @PostMapping("/toJava")
    public AIXMBasicMessageType convertToJava (@RequestBody String xmlContent) throws JAXBException {
        return javaXmlConvertService.xmlFiletoJava(xmlContent);
    }

    @GetMapping("/toXml")
    public AIXMBasicMessageType convertToXml () throws JAXBException {
        return  javaXmlConvertService.obstaclesToXmlFile();
    }

    @GetMapping("/getFeatureCollection")
    public String getFeatureCollection () {
        return obstacleService.getFeatureCollection();
    }


    @PostMapping
    public ResponseEntity<String> addObstacle(@RequestBody ObstacleRequest obstacleRequest) {

        Obstacle obstacle = obstacleService.createObstacleFromObstacleRequest(obstacleRequest);

        Obstacle savedObstacle = obstacleService.addOneObstacle(obstacle);

        return ResponseEntity.ok("Engel başarıyla kaydedildi. ID: " + savedObstacle.getId());

    }


}
