package com.monozel.AixmAndPostgis.controllers;

import com.monozel.AixmAndPostgis.entities.HGMObstacle;
import com.monozel.AixmAndPostgis.entities.StructureType;
import com.monozel.AixmAndPostgis.requests.HGMObstacleRequest;
import com.monozel.AixmAndPostgis.services.HGMObstacleService;
import com.monozel.AixmAndPostgis.services.JavaXmlConvertService;
import com.monozel.AixmAndPostgis.services.ObstacleService;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("/test")
public class TestController {
    private JavaXmlConvertService javaXmlConvertService;

}
