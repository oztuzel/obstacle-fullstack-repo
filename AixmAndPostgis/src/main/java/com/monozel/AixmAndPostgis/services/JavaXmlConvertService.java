package com.monozel.AixmAndPostgis.services;

import aero.aixm.*;
import aero.aixm.message.AIXMBasicMessageType;
import aero.aixm.message.BasicMessageMemberAIXMPropertyType;
import com.cfar.swim.aixm.bind.AixmMarshaller;
import com.cfar.swim.aixm.bind.AixmUnmarshaller;
import com.monozel.AixmAndPostgis.entities.Obstacle;
import lombok.AllArgsConstructor;
import net.opengis.gml.*;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@AllArgsConstructor
public class JavaXmlConvertService {

    private ObstacleService obstacleService;

    public AIXMBasicMessageType toXML () {
        List<Obstacle> obstacleList = obstacleService.getAllObstacles();

//        int obstacleNumber = 1;
        int gmlId = 1;

        // 1-AIXMBasicMesaage olusturuyoruz.

        AIXMBasicMessageType aixmBasicMessageType = new AIXMBasicMessageType();
        aixmBasicMessageType.setId("gml.id" + gmlId);
        gmlId = gmlId +1;

        for(Obstacle obstacle : obstacleList) {
            // 2- BasicMessageMemberAIXMPropertyType olusturuyoruz.
            BasicMessageMemberAIXMPropertyType basicMessageMemberAIXMPropertyType = new BasicMessageMemberAIXMPropertyType();


            // 3- VerticalStructureType olusturuyoruz.
            VerticalStructureType verticalStructureType = new VerticalStructureType();
            verticalStructureType.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            // identifier field'i olusturuyoruz.
            CodeWithAuthorityType codeWithAuthorityType = new CodeWithAuthorityType();
            codeWithAuthorityType.setCodeSpace("urn:uuid:");
            codeWithAuthorityType.setValue(UUID.randomUUID().toString());
            verticalStructureType.setIdentifier(codeWithAuthorityType);


            // 4- VerticalStructureTimeSlicePropertyType olusturuyoruz.
            VerticalStructureTimeSlicePropertyType verticalStructureTimeSlicePropertyType = new VerticalStructureTimeSlicePropertyType();


            // 5- VerticalStructureTimeSliceType olusturuyoruz.
            VerticalStructureTimeSliceType verticalStructureTimeSliceType = new VerticalStructureTimeSliceType();
            verticalStructureTimeSliceType.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            // validTime field'i olusturuyoruz icinde TimePeriod var
            TimePrimitivePropertyType validTime = new TimePrimitivePropertyType();

            TimePeriodType timePeriodType = new TimePeriodType();
            TimePositionType timePositionTypeBeginPosition = new TimePositionType(); // beginPosition fieldini olusturalim
            timePositionTypeBeginPosition.getValue().add("2019-09-12T 00:00:00");
            timePeriodType.setBeginPosition(timePositionTypeBeginPosition);

            timePeriodType.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            TimePositionType timePositionTypeEndPosition = new TimePositionType(); // endPosition kismini olusturalim.
            timePositionTypeEndPosition.setIndeterminatePosition(TimeIndeterminateValueType.UNKNOWN);
            timePeriodType.setEndPosition(timePositionTypeEndPosition);

            JAXBElement<TimePeriodType> timePeriodTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.opengis.net/gml/3.2", "TimePeriod"),
                    TimePeriodType.class,
                    timePeriodType
            );
            validTime.setAbstractTimePrimitive(timePeriodTypeJAXBElement);

            verticalStructureTimeSliceType.setValidTime(validTime);

            // interpretation, sequenceNumber, correctionNumber fields
            verticalStructureTimeSliceType.setInterpretation("BASELINE");
            verticalStructureTimeSliceType.setCorrectionNumber(0L);
            verticalStructureTimeSliceType.setSequenceNumber(1L);


            // featureLifeTime field
            TimePrimitivePropertyType featureTime = new TimePrimitivePropertyType();

            TimePeriodType timePeriodType2 = new TimePeriodType();
            TimePositionType timePositionTypeBeginPosition2 = new TimePositionType(); // beginPosition fieldini olusturalim
            timePositionTypeBeginPosition2.getValue().add("2019-09-12T 00:00:00");
            timePeriodType2.setBeginPosition(timePositionTypeBeginPosition2);

            timePeriodType2.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            TimePositionType timePositionTypeEndPosition2 = new TimePositionType(); // endPosition kismini olusturalim.
            timePositionTypeEndPosition2.setIndeterminatePosition(TimeIndeterminateValueType.UNKNOWN);
            timePeriodType2.setEndPosition(timePositionTypeEndPosition2);

            JAXBElement<TimePeriodType> timePeriodTypeJAXBElement2 = new JAXBElement<>(
                    new QName("http://www.opengis.net/gml/3.2", "TimePeriod"),
                    TimePeriodType.class,
                    timePeriodType2
            );
            featureTime.setAbstractTimePrimitive(timePeriodTypeJAXBElement2);

            verticalStructureTimeSliceType.setFeatureLifetime(featureTime);


            // name field'ini olusturalim duzenleyelim.  JAXBElement<TextNameType> verticalStructureName
            TextNameType type = new TextNameType();
            type.setValue(obstacle.getAixm_name());
            JAXBElement<TextNameType> textNameTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "name"),
                    TextNameType.class,
                    type
            );
            verticalStructureTimeSliceType.setVerticalStructureName(textNameTypeJAXBElement);

            // type kismi
            CodeVerticalStructureType codeVerticalStructureType = new CodeVerticalStructureType();
            codeVerticalStructureType.setValue(obstacle.getType());
            JAXBElement<CodeVerticalStructureType> codeVerticalStructureTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "type"),
                    CodeVerticalStructureType.class,
                    codeVerticalStructureType
            );
            verticalStructureTimeSliceType.setType(codeVerticalStructureTypeJAXBElement);


            // lighted field
            CodeYesNoType codeYesNoType = new CodeYesNoType();
            if(obstacle.getLighting() == null) {
                codeYesNoType.setValue("NO");
            }else {
                codeYesNoType.setValue(obstacle.getLighting());
            }
            JAXBElement<CodeYesNoType> codeYesNoTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "lighted"),
                    CodeYesNoType.class,
                    codeYesNoType
            );
            verticalStructureTimeSliceType.setLighted(codeYesNoTypeJAXBElement);


            // group field
            CodeYesNoType group = new CodeYesNoType();
            group.setValue(obstacle.getGroup());
            JAXBElement<CodeYesNoType> groupJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "group"),
                    CodeYesNoType.class,
                    group
            );
            verticalStructureTimeSliceType.setGroup(groupJAXBElement);

            // part fieldini asagidakileri olusturup set edecez.

            // 6- VerticalStructurePartPropertyType
            VerticalStructurePartPropertyType verticalStructurePartPropertyType = new VerticalStructurePartPropertyType();

            // 7- VerticalStructurePartType olusturuyoruz.
            VerticalStructurePartType verticalStructurePartType = new VerticalStructurePartType();
            verticalStructurePartType.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            // a) verticalExtent kismi
            ValDistanceType valDistanceType = new ValDistanceType();
            valDistanceType.setUom("FT");
            valDistanceType.setValue(obstacle.getHeight());
//            valDistanceType.setValue(BigDecimal.valueOf(obstacle.getHeight()));
            JAXBElement<ValDistanceType> valDistanceTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "verticalExtent"),
                    ValDistanceType.class,
                    valDistanceType
            );
            verticalStructurePartType.setVerticalExtent(valDistanceTypeJAXBElement);

            // b) type i verticalStructureTimeSlice icinde olusturmustuk aynisi
            verticalStructurePartType.setType(codeVerticalStructureTypeJAXBElement);

            // c) designator field'i
            TextDesignatorType textDesignatorType = new TextDesignatorType();
            textDesignatorType.setValue(obstacle.getDesignator()); // + obstacleNumber kismini kestim
//            obstacleNumber = obstacleNumber+1;
            JAXBElement<TextDesignatorType> textDesignatorTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "designator"),
                    TextDesignatorType.class,
                    textDesignatorType
            );
            verticalStructurePartType.setDesignator(textDesignatorTypeJAXBElement);

            // d) horizontalProjection_location field (icinde ElevatedPoint var)
            ElevatedPointPropertyType elevatedPointPropertyType = new ElevatedPointPropertyType();

            ElevatedPointType elevatedPointType = new ElevatedPointType();  // elevatedpointtype class'i pointtype classindan o da opengis.gml.PointType class'indan extent eder bu sayede pos vb kullaniyoruz.
            DirectPositionType coordinates = new DirectPositionType();
            coordinates.getValue().add(obstacle.getPoint().getCoordinate().getX());
            coordinates.getValue().add(obstacle.getPoint().getCoordinate().getY());

//  b          coordinates.getValue().add(obstacle.getLatitude());
//  b          coordinates.getValue().add(obstacle.getLongitude());
// a          coordinates.getValue().add(obstacle.getPoint().getX());
            elevatedPointType.setPos(coordinates);
            elevatedPointType.setSrsName("urn:ogc:def:crs:EPSG::" + obstacle.getPoint().getSRID());

            elevatedPointType.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            ValDistanceVerticalType valDistanceVerticalType = new ValDistanceVerticalType();

            valDistanceVerticalType.setValue(String.valueOf(obstacle.getPoint().getCoordinate().getZ()));

            valDistanceVerticalType.setUom("FT");
            JAXBElement<ValDistanceVerticalType> valDistanceJaxb = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "elevation"),
                    ValDistanceVerticalType.class,
                    valDistanceVerticalType
            );
            elevatedPointType.setElevation(valDistanceJaxb);

            elevatedPointPropertyType.setElevatedPoint(elevatedPointType);
            JAXBElement<ElevatedPointPropertyType> elevatedPointPropertyTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "horizontalProjection_location"),
                    ElevatedPointPropertyType.class,
                    elevatedPointPropertyType
            );

            verticalStructurePartType.setHorizontalProjectionLocation(elevatedPointPropertyTypeJAXBElement);


            // e) lighting field (icinde LightElement var)
            LightElementPropertyType lightElementPropertyType = new LightElementPropertyType();

            LightElementType lightElement = new LightElementType();
            CodeColourType colour = new CodeColourType();
            colour.setValue(obstacle.getColour());
            JAXBElement<CodeColourType> lightElementColour = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "colour"),
                    CodeColourType.class,
                    colour
            );
            lightElement.setColour(lightElementColour);

            lightElement.setId("gml.id" + gmlId);
            gmlId = gmlId + 1;

            lightElementPropertyType.setLightElement(lightElement);

            verticalStructurePartType.getLighting().add(lightElementPropertyType);


            // VerticalStructurePart burada bitti. bunu VerticalStructurePartPropertyType icindeki field'a veriyoruz.
            verticalStructurePartPropertyType.setVerticalStructurePart(verticalStructurePartType);

            // olusturdugumuz VerticalStructurePartPropertyType'i, VerticalStructureTimeSliceType icindeki ilgili field'a veriyoruz.
            verticalStructureTimeSliceType.getPart().add(verticalStructurePartPropertyType);

            // olusturdugumuz diger field'larla beraber verticalStructureTimeSliceType (5. kisimda) olusturmamizda tamamlandi. bunu verticalStructureTimeSlicePropertyType'da ilgili field'a set ediyoruz
            verticalStructureTimeSlicePropertyType.setVerticalStructureTimeSlice(verticalStructureTimeSliceType);

            // bunu VerticalStructureType daki ilgili field'a set ediyoruz.
            verticalStructureType.getTimeSlice().add(verticalStructureTimeSlicePropertyType);

            // VerticalStructureType olusturmamizda bitti bunu jaxbelement'e donusturup ilgili field'a set ediyoruz
            JAXBElement<VerticalStructureType> verticalStructureTypeJAXBElement = new JAXBElement<>(
                    new QName("http://www.aixm.aero/schema/5.1.1", "VerticalStructure"),
                    VerticalStructureType.class,
                    verticalStructureType
            );
            basicMessageMemberAIXMPropertyType.setAbstractAIXMFeature(verticalStructureTypeJAXBElement);

            // AIXMBasicMessageType'daki hasMember field'a bitirdigimiz basicMessageMemberAIXMPropertyType'i set ediyoruz.
            aixmBasicMessageType.getHasMember().add(basicMessageMemberAIXMPropertyType);

        }

        return aixmBasicMessageType;
    }

    // onceden olusturulmus xml dosyalari aixm 5.1.0 ile olusturuldugu icin o xml dosyalarini okumak icin
    // pom.xml de dependency olarak aixm-jaxb mvn reponun verison 5.1.0-beta5 'i kullan

    // xml dosya icerigini react ile on yuzde okuyup post request ile backend'e yolluyoruz ve burada o dosyayi isliyoruz.
    public AIXMBasicMessageType xmlFiletoJava (String xmlContent) throws JAXBException {

        obstacleService.deleteAllObstacles();

        // StringReader kullanarak XML içeriğini oku
        StringReader reader = new StringReader(xmlContent);

        AixmUnmarshaller unmarshaller = new AixmUnmarshaller();
        JAXBElement<AIXMBasicMessageType> aixmBasicMessageTypeJaxbElement =
                (JAXBElement<AIXMBasicMessageType>) unmarshaller.unmarshal(new StreamSource(reader));

        AIXMBasicMessageType aixmBasicMessageType1 = aixmBasicMessageTypeJaxbElement.getValue();
        List<BasicMessageMemberAIXMPropertyType> hasMemberList =  aixmBasicMessageType1.getHasMember();

        for (BasicMessageMemberAIXMPropertyType b : hasMemberList) {
            Obstacle obstacle = new Obstacle();

            VerticalStructureType verticalStructureType = (VerticalStructureType) b.getAbstractAIXMFeature().getValue();

            obstacle.setAixm_name(
                    verticalStructureType
                            .getTimeSlice().get(0)
                            .getVerticalStructureTimeSlice()
                            .getVerticalStructureName().getValue().getValue()
            );

            obstacle.setGroup(
                    verticalStructureType
                            .getTimeSlice().get(0).
                            getVerticalStructureTimeSlice()
                            .getGroup().getValue().getValue()
            );

            obstacle.setType(
                    verticalStructureType
                            .getTimeSlice().get(0)
                            .getVerticalStructureTimeSlice()
                            .getType().getValue().getValue()
            );

            if(
                    verticalStructureType
                        .getTimeSlice().get(0)
                        .getVerticalStructureTimeSlice()
                        .getLighted() != null   ) {

                obstacle.setLighting(
                        verticalStructureType
                                .getTimeSlice().get(0)
                                .getVerticalStructureTimeSlice()
                                .getLighted().getValue().getValue()
                );

            }else {
                obstacle.setLighting("NO");
            }

            obstacle.setHeight(
                    verticalStructureType
                            .getTimeSlice().get(0)
                            .getVerticalStructureTimeSlice()
                            .getPart().get(0)
                            .getVerticalStructurePart()
                            .getVerticalExtent().getValue().getValue()
            );
// designator kisminda LTCT1 degerindeki sayi degerlerini cikartip sadece string kisimlari kaydediyoruz.
            // sayi degerini cikarmamak gerekli
            String designator = verticalStructureType
                    .getTimeSlice().get(0)
                    .getVerticalStructureTimeSlice()
                    .getPart().get(0)
                    .getVerticalStructurePart()
                    .getDesignator()
                    .getValue().getValue();
//            StringBuilder result = new StringBuilder();
//            for(char c: designator.toCharArray()){
//                if(!Character.isDigit(c)){
//                    result.append(c);
//                }
//            }
            obstacle.setDesignator(
                designator
            );


            // Point (geometry type) field'ini set ettigimiz yer...
            String convertedStringLatitude = convertDMS(verticalStructureType
                    .getTimeSlice().get(0)
                    .getVerticalStructureTimeSlice()
                    .getPart().get(0)
                    .getVerticalStructurePart()
                    .getHorizontalProjectionLocation().getValue()
                    .getElevatedPoint()
                    .getPos()
                    .getValue().get(0));
            double convertedLatitudeDMS = toDoubleDMS(convertedStringLatitude);
            obstacle.setLatitudeDMS(convertedLatitudeDMS);

            String convertedStringLongitude = convertDMS(verticalStructureType
                    .getTimeSlice().get(0)
                    .getVerticalStructureTimeSlice()
                    .getPart().get(0)
                    .getVerticalStructurePart()
                    .getHorizontalProjectionLocation().getValue()
                    .getElevatedPoint()
                    .getPos()
                    .getValue().get(1));
            double convertedLongitudeDMS = toDoubleDMS(convertedStringLongitude);
            obstacle.setLongitudeDMS(convertedLongitudeDMS);

            GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(),4326);
            org.locationtech.jts.geom.Point point = geometryFactory.createPoint(
                    new Coordinate(
                            verticalStructureType
                                    .getTimeSlice().get(0)
                                    .getVerticalStructureTimeSlice()
                                    .getPart().get(0)
                                    .getVerticalStructurePart()
                                    .getHorizontalProjectionLocation().getValue()
                                    .getElevatedPoint()
                                    .getPos()
                                    .getValue().get(0),
                            verticalStructureType
                                    .getTimeSlice().get(0)
                                    .getVerticalStructureTimeSlice()
                                    .getPart().get(0)
                                    .getVerticalStructurePart()
                                    .getHorizontalProjectionLocation().getValue()
                                    .getElevatedPoint()
                                    .getPos()
                                    .getValue().get(1)
                    ));
            obstacle.setPoint(point);

            // latitudeDMS ve longitudeDMS'i set ettigimiz yer

            // elevation'i set ediyoruz
            obstacle.setElevation(
                    Double.valueOf(
                            verticalStructureType
                                    .getTimeSlice().get(0)
                                    .getVerticalStructureTimeSlice()
                                    .getPart().get(0)
                                    .getVerticalStructurePart()
                                    .getHorizontalProjectionLocation().getValue()
                                    .getElevatedPoint()
                                    .getElevation()
                                    .getValue().getValue()
                    ));

            // colour field'i ayarliyoruz.
        if ( verticalStructureType
                .getTimeSlice().get(0)
                .getVerticalStructureTimeSlice()
                .getPart().get(0)
                .getVerticalStructurePart()
                .getLighting().get(0)
                .getLightElement()
                .getColour() != null
        ) {

            obstacle.setColour(
                    verticalStructureType
                            .getTimeSlice().get(0)
                            .getVerticalStructureTimeSlice()
                            .getPart().get(0)
                            .getVerticalStructurePart()
                            .getLighting().get(0)
                            .getLightElement()
                            .getColour()
                            .getValue().getValue()
            );
        }

            obstacleService.addOneObstacle(obstacle);
        }

        return aixmBasicMessageTypeJaxbElement.getValue();
    }


    public AIXMBasicMessageType obstaclesToXmlFile () throws JAXBException {
        AIXMBasicMessageType aixmBasicMessageType = toXML();
        JAXBElement<AIXMBasicMessageType> jaxbElement = new JAXBElement<>(
                new QName("http://www.aixm.aero/schema/5.1/message","AIXMBasicMessage"),
                AIXMBasicMessageType.class,
                aixmBasicMessageType
        );
        AixmMarshaller marshaller = new AixmMarshaller();
        marshaller.marshal(jaxbElement,new File("obstacle.xml"));

        return aixmBasicMessageType;
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


}
