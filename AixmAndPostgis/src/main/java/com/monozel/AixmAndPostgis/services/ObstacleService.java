package com.monozel.AixmAndPostgis.services;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;
import com.monozel.AixmAndPostgis.entities.Obstacle;
import com.monozel.AixmAndPostgis.repositories.HGMObstacleRepository;
import com.monozel.AixmAndPostgis.repositories.ObstacleRepository;
import com.monozel.AixmAndPostgis.requests.ObstacleDistanceRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.locationtech.jts.geom.Point;
import com.monozel.AixmAndPostgis.requests.ObstacleRequest;
import lombok.AllArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ObstacleService {
    private ObstacleRepository obstacleRepository;
    private HGMObstacleRepository hgmObstacleRepository;

    public Obstacle getOneObstacle (Long id) {
        return obstacleRepository.findById(id).orElse(null);
    }

    public List<Obstacle> getAllObstacles () {
        return obstacleRepository.findAll();
    }

    public void deleteAllObstacles (){
        obstacleRepository.deleteAll();
    }

    public Obstacle addOneObstacle (Obstacle obstacle) {
        // db de ayni point ayni height ayni elevation ayni type ve ayni designator'e sahip baska bir obstacle varsa onu tekrar kaydetmiyoruz.
        if (obstacleRepository.existsByPointAndHeightAndElevationAndTypeAndDesignator(obstacle.getPoint(),obstacle.getHeight(),obstacle.getElevation(),obstacle.getType(),obstacle.getDesignator())) {
//            List<Obstacle> list = obstacleRepository.findByPointAndHeightAndElevationAndTypeAndDesignator(obstacle.getPoint(),obstacle.getHeight(),obstacle.getElevation(),obstacle.getType(),obstacle.getDesignator());
//            System.out.println(list);

            return null;
        }
        return obstacleRepository.save(obstacle);

    }

    public Obstacle createObstacleFromObstacleRequest (ObstacleRequest obstacleRequest) {
        Obstacle obstacle = new Obstacle();
        obstacle.setLighting(obstacleRequest.getLighting());
        obstacle.setType(obstacleRequest.getType());
        obstacle.setGroup(obstacleRequest.getGroup());
        obstacle.setColour(obstacleRequest.getColour());
        obstacle.setAixm_name(obstacleRequest.getAixm_name());
        obstacle.setDesignator(obstacleRequest.getDesignator());
        obstacle.setHeight(obstacleRequest.getHeight());

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
        org.locationtech.jts.geom.Point point =
                geometryFactory.createPoint(
                    new Coordinate(
                            obstacleRequest.getLatitude(),
                            obstacleRequest.getLongitude(),
                            obstacleRequest.getElevation()));

        obstacle.setPoint(point);

        return obstacle;

    }


    public ObstacleRequest findById(Long id) {
        Optional<Obstacle> obstacle =  obstacleRepository.findById(id);
        return new ObstacleRequest(obstacle.orElse(null));
    }


    // point'i hgm obstacle'daki point'i ile yuzde yuz eslesmeyenleri aliyoruz.
    public Map<Long, List<Long>> findSimilarPoints() {
        List<Obstacle> obstacleList =  obstacleRepository.findSimilarPoints();
        List<HGMObstacle> hgmObstacleList = hgmObstacleRepository.findSimilarPoints();

        // Obstacle ve HGMObstacle id'lerini eşleştirmek için bir Map oluştur
        Map<Long, List<Long>> resultMap = new HashMap<>();

        // LatitudeDMS ve LongitudeDMS'yi filtreleme kriterlerine göre kontrol et
        for (Obstacle obstacle : obstacleList) {
            resultMap.put(obstacle.getId(), new ArrayList<>());
            for (HGMObstacle hgmObstacle : hgmObstacleList) {
                if (checkFilterConditions(obstacle, hgmObstacle)) {
                    resultMap.get(obstacle.getId()).add(hgmObstacle.getId());
                }
            }
        }

//        Map<Long, List<Long>> nonEmptyLists =
              return  resultMap.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//        System.out.println("Dms koordinatinda virgulden sonraki tek hanesi ayni olanlarin sayisi: " + nonEmptyLists.size());
    }


    public  List<ObstacleDistanceRequest> findNearestTenPoints(Long obstacleId) {
        List<Obstacle> obstacleList = obstacleRepository.findNearestObstacles(obstacleId);
        List<ObstacleRequest> obstacleRequestList = new ArrayList<>();
        for(Obstacle obstacle : obstacleList) {
            obstacleRequestList.add(new ObstacleRequest(obstacle));
        }

        // buranin altinda yaptigimiz islem o2.designator ve distance'i sql sorgusuyla cektik ve yukarida cektigimiz o2 ile designatorlari kiyaslayip equal olanlari bulup ObstacleDistanceRequest olusturduk.
        List<ObstacleDistanceRequest> obstacleDistanceRequestList = new ArrayList<>();

        List<Object[]> designatorAndDistance = obstacleRepository.findNearestObstaclesDistance(obstacleId);

        for (Object[] result : designatorAndDistance) {
            String designator = (String) result[0];
            Double distance = (Double) result[1];

            ObstacleRequest obstacleRequest = obstacleRequestList.stream()
                    .filter(obstacle -> obstacle.getDesignator().equals(designator))
                    .findFirst()
                    .orElse(null);

            if (obstacleRequest != null) {
                ObstacleDistanceRequest obstacleDistanceRequest = new ObstacleDistanceRequest();
                obstacleDistanceRequest.setObstacleRequest(obstacleRequest);
                obstacleDistanceRequest.setDistance(distance);

                obstacleDistanceRequestList.add(obstacleDistanceRequest);
            }
        }

        return obstacleDistanceRequestList;
    }

    // dms koordinatindaki virgule kadarki kisimlari ayni olan obstacle'larin idlerini listeliyoruz. {"1": [23,36,45]} gibi bir sonuc dondurur.
    public Map<Long, List<Long>> findSamePointsWithInteger() {
        List<Obstacle> obstacleList =  obstacleRepository.findSimilarPoints();
        List<HGMObstacle> hgmObstacleList = hgmObstacleRepository.findSimilarPoints();

        Map<Long, List<Long>> resultMap = findSimilarPoints();

        // resulmap'teki Long degerlerini (obstacleId) alip obstacleList'ten cikariyoruz.
        Set<Long> obstacleIdsToRemove = resultMap.keySet(); // bu set mapteki Long degerlerini icerir.
        obstacleList.removeIf(obstacle -> obstacleIdsToRemove.contains(obstacle.getId()));

        // resultMap'deki List<Long> hgmObstacleId listesini alip bu degerleri hgmObstacleList'ten cikariyoruz.
        Set<Long> hgmObstacleIdsToRemove = resultMap.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());
        hgmObstacleList.removeIf(hgmObstacle -> hgmObstacleIdsToRemove.contains(hgmObstacle.getId()));


        // yeni sonuclarimiz icin yeni HashMap olusturuyoruz.
        Map<Long,List<Long>> newResult = new HashMap<>();

        // LatitudeDMS ve LongitudeDMS'yi filtreleme kriterlerine göre kontrol et
        for (Obstacle obstacle : obstacleList) {
            newResult.put(obstacle.getId(), new ArrayList<>());
            for (HGMObstacle hgmObstacle : hgmObstacleList) {
                if (checkOtherFilterConditions(obstacle, hgmObstacle)) {
                    newResult.get(obstacle.getId()).add(hgmObstacle.getId());
                }
            }
        }

        return newResult.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }


    // burada enr obstacle'lari kendi icinde bir kiyaslamaya sokuyoruz ve 30 metreden kisa mesafedekileri listeliyoruz.
    // { "3163": [26, 326], ...... } gibi bir sonuc dondurur. yani 3163 id'li obstacle 26 id'li ve 326 id'li obstacle ile 30 metreden kisa mesafededir.
    public Map<Long,List<Long>> findObstaclesWithin30MetersForEach() {
        List<Obstacle> allObstacles = obstacleRepository.findAll();
        Map<Long,List<Long>> result = new HashMap<>();
        Set<String> processedPairs = new HashSet<>(); // "4326":[126] , "126":[4326] seklindeki ikilikleri onlemek icin set kullaniyoruz.

        for (Obstacle obstacle : allObstacles) {
            List<Long> nearbyObstacles = obstacleRepository.findObstaclesWithin30Meters(obstacle.getPoint(), obstacle.getId());
            if (!nearbyObstacles.isEmpty()) {
//                result.put(obstacle.getId(),nearbyObstacles);

                // bu kisimlar ikilikleri gidermek icin yukaridaki set ile calisan kisim ama ikiliklerin olmasi cok birseyi degistirmez.
                for (Long nearbyObstacleId : nearbyObstacles) {
                    String pairKey1 = obstacle.getId() + "-" + nearbyObstacleId;
                    String pairKey2 = nearbyObstacleId + "-" + obstacle.getId();

                    if (!processedPairs.contains(pairKey1) && !processedPairs.contains(pairKey2)) {
                        result.put(obstacle.getId(), nearbyObstacles);
                        processedPairs.add(pairKey1);
                        processedPairs.add(pairKey2);
                    }
                }
            }
        }
        return result;
    }

    public List<ObstacleRequest> findObstaclesWith30 () {
        List<Obstacle> obstacleList = obstacleRepository.findObstaclesWithin30M();
        List<ObstacleRequest> obstacleRequestList = new ArrayList<>();
        for(Obstacle obstacle : obstacleList) {
            obstacleRequestList.add(new ObstacleRequest(obstacle));
        }
        return obstacleRequestList;
    }


    public String getFeatureCollection () {
        JSONArray jsonArray = new JSONArray();

        List<String> stringList = obstacleRepository.getFeatureCollectionInObstacle();

        for (String s : stringList) {
            try {
                JSONObject jsonObject = new JSONObject(s);

                // 'features' listesini al
                JSONArray featuresArray = jsonObject.getJSONArray("features");

                // Her bir feature için 'geometry' alanindaki string'i json objesine ceviriyoruz
                for (int i = 0; i < featuresArray.length(); i++) {
                    JSONObject featureObject = featuresArray.getJSONObject(i);

                    String geometryString = featureObject.getString("geometry");

                    JSONObject geometryObject = new JSONObject(geometryString);

                    // string'den json a donusturdugumuz yeri tekrardan geometry kismina put ile koyuyoruz.
                    featureObject.put("geometry", geometryObject);
                }

                jsonArray.put(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
    }



    // burada dms koordinatinda virgulden sonra tek basamak olacak sekilde sayiyi yuvarlamadan kisaltan fonksiyonu kullaniyoruz ve sonra degerleri karsilastiriyoruz.
    private boolean checkFilterConditions(Obstacle obstacle, HGMObstacle hgmObstacle) {

        double obstacleLatitudeDMS = truncatedValue(obstacle.getLatitudeDMS() );
        double obstacleLongitudeDMS =  truncatedValue(  obstacle.getLongitudeDMS());
        double hgmLatitudeDMS = truncatedValue( hgmObstacle.getLatitudeDMS()) ;
        double hgmLongitudeDMS = truncatedValue(  hgmObstacle.getLongitudeDMS());

        return obstacleLatitudeDMS == hgmLatitudeDMS && obstacleLongitudeDMS == hgmLongitudeDMS;
    }

    // burada dms koordinatinda virgule kadarki integer degerini yuvarlamadan kesiyoruz ve birbirleriyle kiyasliyoruz.
    private boolean checkOtherFilterConditions (Obstacle obstacle, HGMObstacle hgmObstacle) {
        int obstacleLatitudeDMS = (int) obstacle.getLatitudeDMS();
        int obstacleLongitudeDMS = (int) obstacle.getLongitudeDMS();
        int hgmLatitudeDMS = (int) hgmObstacle.getLatitudeDMS();
        int hgmLongitudeDMS = (int) hgmObstacle.getLongitudeDMS();

        return obstacleLatitudeDMS == hgmLatitudeDMS && obstacleLongitudeDMS == hgmLongitudeDMS;
    }


    // 446678.78 gibi dms koordinati alip virgulden sonra tek haneye dusuruyoruz. 446678.7 gibi (yuvarlama yapmiyoruz.)
    private double truncatedValue(double value){
        // virgulden sonra 2 basamak degil tek basamak olmasini istedigimiz icin double degerini kisaltiyoruz. yuvarlama yapmadan kisaltmak icin string'e cevirip kisalttim.
        NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);

        numberFormat.setRoundingMode(RoundingMode.DOWN); // burada .down secerek truncate yap rounded yapma dedik

        numberFormat.setMaximumFractionDigits(1); // virgulden sonra tek basamak olsun dedik.
        // Truncate işlemini uygula dedigimiz yer burasi onceki yerler sartlari olusturdugumuz yer.
        String formattedValue = numberFormat.format(value);

        try {
            double truncatedValue = numberFormat.parse(formattedValue).doubleValue();
//            System.out.println(truncatedValue);
            return truncatedValue;
        } catch (ParseException e) {
            System.out.println("DMS koordinati virgulden sonra tek basamak olacak sekilde truncate yaparken hata olustu.");
            e.printStackTrace();
        };
        return 0;
    }


}
