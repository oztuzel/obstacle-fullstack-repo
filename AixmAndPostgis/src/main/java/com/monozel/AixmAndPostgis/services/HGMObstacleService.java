package com.monozel.AixmAndPostgis.services;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;
import com.monozel.AixmAndPostgis.entities.StructureType;
import com.monozel.AixmAndPostgis.repositories.HGMObstacleRepository;
import com.monozel.AixmAndPostgis.requests.HGMObstacleRequest;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@AllArgsConstructor
public class HGMObstacleService {

    private HGMObstacleRepository hgmObstacleRepository;

    public List<HGMObstacle> readExcel(MultipartFile file) {
        List<HGMObstacle> hgmObstacleList = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = sheet.iterator();
            rowIterator.next(); // Başlık satırını atla

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();


                HGMObstacle hgmObstacle = new HGMObstacle();
                // obstacle name'i set ediyoruz
                if(row.getCell(1) != null){
                    hgmObstacle.setObstacleName(row.getCell(1).getStringCellValue());
                }

                // obstacle type'i StructureType enum'indan cekiyoruz engtip_id'den gelen int degerine gore
                if(row.getCell(2) != null) {
                    hgmObstacle.setStructureType(StructureType.getStructureTypeByCode((int) row.getCell(2).getNumericCellValue()).getDescription());
                }


                // obstacle height'i set ediyoruz. binayuk ve engyuk hucrelerinin toplami == height
                // ama metreyi feet degerine donusturup o sekilde set ediyoruz. (1 metre = 3.28084 feet)
                double height = 0;
                double elevation;
                if(row.getCell(8) != null) {
                    height = row.getCell(8).getNumericCellValue() + row.getCell(9).getNumericCellValue();
                }else {
                    height = row.getCell(9).getNumericCellValue();
                }
                if(row.getCell(15) != null){
                    elevation = height + row.getCell(15).getNumericCellValue();
                }else{
                    elevation = height;
                }

                hgmObstacle.setHeight(Math.round(height * (3.28084)));  // burada metreyi feet'e cevirip kaydediyoruz.
                double roundedElevation = (double) Math.round(elevation * 3.28084);


                // point kismini set edelim (icinde new Coordinate(x,y,z) z=elevation, x ve y de ham kordinatlardir, derece dakika saniye degildir.
                // normaldeki degerlerin eslesmesi icin burada once ham koordinati derece dakika saniyeye cevirip sonrasinda tekrar ham koordinata cevirecez.
                double decimalLongitude = row.getCell(6).getNumericCellValue(); // boylam
                String convertedStringLongitude = convertDMS(decimalLongitude);
                        // longitudeDMS field'ini set ediyoruz.
                double longitudeDMS = toDoubleDMS(convertedStringLongitude);
                hgmObstacle.setLongitudeDMS(longitudeDMS);

                double parsedStringLongitude = parseDMS(convertedStringLongitude);


                double decimalLatitude = row.getCell(7).getNumericCellValue();  // enlem
                String convertedStringLatitude = convertDMS(decimalLatitude);
                        // latitudeDMS field'ini set ediyoruz.
                double latitudeDMS = toDoubleDMS(convertedStringLatitude);
                hgmObstacle.setLatitudeDMS(latitudeDMS);

                double parsedStringLatitude = parseDMS(convertedStringLatitude);



                    // new Coordinate ( longitude, latitude) seklinde olusuturulmali
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
                org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                        new Coordinate(
                                 parsedStringLatitude,
                                 parsedStringLongitude
                        ));
                hgmObstacle.setPoint(point);

                // elevation'i set ettigimiz yer
                hgmObstacle.setElevation(roundedElevation);

                // lighting kismi 1=var, 2=yok, 0=no info
                if(row.getCell(5) != null ){
                    if(row.getCell(5).getNumericCellValue() == 1) {
                        hgmObstacle.setLighting("YES");
                    }else if(row.getCell(5).getNumericCellValue() == 2){
                        hgmObstacle.setLighting("NO");
                    }
                }


                if(row.getCell(16)!=null ){
                    if(!row.getCell(16).getBooleanCellValue()){ // egerki YANLIS degeri varsa db ye kaydediyoruz.
                        hgmObstacleList.add(hgmObstacle);
                        addOneHGMObstacle(hgmObstacle);
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return hgmObstacleList;
    }

    public HGMObstacle addOneHGMObstacle (HGMObstacle hgmObstacle) {
        if(hgmObstacleRepository.existsByPointAndElevationAndHeightAndObstacleNameAndStructureTypeAndLighting(
                hgmObstacle.getPoint(),
                hgmObstacle.getElevation(),
                hgmObstacle.getHeight(),
                hgmObstacle.getObstacleName(),
                hgmObstacle.getStructureType(),
                hgmObstacle.getLighting())
        ){
            return null;
        }

        return hgmObstacleRepository.save(hgmObstacle);
    }

    public List<HGMObstacleRequest> getAllHGMObstacleRequests() {
        List<HGMObstacle> hgmObstacleList = hgmObstacleRepository.findAll();
        List<HGMObstacleRequest> hgmObstacleRequests = new ArrayList<>();

        for(HGMObstacle hgmObstacle : hgmObstacleList){
            hgmObstacleRequests.add(new HGMObstacleRequest(hgmObstacle));
        }

        return hgmObstacleRequests;
    }

    public Map<Long, List<Long>> findEqualsPoints() {
        List<Long[]> resultList = hgmObstacleRepository.findEqualsPoints();
        Map<Long, List<Long>> resultMap = new HashMap<>();
        for (Long[] result : resultList) {
            Long obstacleId = (Long) result[0];
            Long hgmObstacleId = (Long) result[1];

            if (!resultMap.containsKey(obstacleId)) {
                resultMap.put(obstacleId, new ArrayList<>());
            }

            resultMap.get(obstacleId).add(hgmObstacleId);
        }
        return resultMap;


    }

    // ondalik koordinati alir ve "40,44,52.78" seklinde bir string degeri dondurur. virgulden sonraki 2. basamak en yakin degere yuvarlanmis halidir
    public String convertDMS (double decimalCoordinate) {
        // derece
        int degrees = (int) decimalCoordinate;

        // dakika
        double decimalMinutes = (decimalCoordinate - degrees ) * 60;
        int minutes = (int) decimalMinutes;

        // saniye
        double decimalSeconds = (decimalMinutes - minutes) * 60;

        // derece dakika ve saniye virgulle ayrilmis
        return String.format(Locale.US,"%d,%d,%.2f",degrees,minutes,decimalSeconds);  // Locale.US diyerek saniyedeki ondalik ayrimini "." ile yapmasini soyledik. Soylemedigimizde "," kullaniyordu.
    }

    // "40,44,52.78" seklinde bir string degerini alir ve 404452.78 seklinde bir double verisi dondurur.
    public double toDoubleDMS (String fromConvertedDMS) {
        String[] parts = fromConvertedDMS.split(",");

        // Derece, dakika ve saniye değerlerini çıkar
        double degrees = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]);
        double seconds = Double.parseDouble(parts[2]);

        // Double değeri oluştur
        double result = degrees * 10000 + minutes * 100 + seconds;

        return result;
    }

    // "40,44,52.78" seklinde bir dms string degerini alir ve bunu 40.7479944444 seklinde ondalik kordinata cevirir
    public double parseDMS (String dmsString) {
        String[] parts = dmsString.split(",");
        int degrees = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        double seconds = Double.parseDouble(parts[2]);

        double firstResult =  degrees+ minutes/60.0 + seconds/3600.0;

        return Math.round(firstResult * 1e10) / 1e10; // virgulden sonra 10 basamak olan halini donduruyoruz

    }

    public HGMObstacleRequest findById(Long id) {
        Optional<HGMObstacle> hgmObstacle =  hgmObstacleRepository.findById(id);
        return new HGMObstacleRequest(hgmObstacle.orElse(null));
    }

    public List<HGMObstacleRequest> findSimilarPoints() {
        List<HGMObstacle> hgmObstacleList =  hgmObstacleRepository.findSimilarPoints();
        List<HGMObstacleRequest> hgmObstacleRequestList = new ArrayList<>();
        for(HGMObstacle hgmObstacle : hgmObstacleList) {
            hgmObstacleRequestList.add(new HGMObstacleRequest(hgmObstacle));
        }
        return hgmObstacleRequestList;
    }

    public  List<HGMObstacleRequest> findNearestTenPoints(Long hgmObstacleId) {
        List<HGMObstacle> results = hgmObstacleRepository.findNearestHGMObstacles(hgmObstacleId);
        List<HGMObstacleRequest> hgmObstacleRequestList = new ArrayList<>();
        for(HGMObstacle hgmObstacle : results) {
            hgmObstacleRequestList.add(new HGMObstacleRequest(hgmObstacle));
        }

        // burada da designator ve distance alip uygun bir sekilde gonderiyoruz.


        return hgmObstacleRequestList;
    }
}
