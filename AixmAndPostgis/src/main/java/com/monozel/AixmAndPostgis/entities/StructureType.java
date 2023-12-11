package com.monozel.AixmAndPostgis.entities;

public enum StructureType {
    UNKNOWN(0, "Bilinmiyor"),
    ANTEN(1, "Anten"),
    LIGHTING_POLE(2, "Aydınlatma Direği"),
    CHIMNEY(3, "Baca"),
    DAM(4, "Baraj Bendi/Kret"),
    FLAG_POLE(5, "Bayrak Direği"),
    TRANSFORMER_BUILDING(6, "Bina Tipi Trafo"),
    BUILDING(7, "Bina"),
    TRANSMISSION_LINE_TOWER(8, "Blodin Hattı/Çelik Taşıyıcı"),
    MOSQUE_MINARET(9, "Cami Minaresi"),
    VERTICAL_TANK(10, "Dikey Tank"),
    POLE_TRANSFORMER(11, "Direk Tipi Trafo"),
    POWER_PLANT(12, "Elektrik Santrali"),
    DISTRIBUTION_LINE_POLE(13, "Enerji Dağıtım Hattı Direği"),
    TRANSMISSION_LINE_POLE(14, "Enerji İletim Hattı Direği"),
    MOBILE_COMMUNICATION_TOWER(15, "Mobil Elektronik Haberleşme Kulesi"),
    AIR_TRAFFIC_CONTROL_TOWER(16, "Hava Trafik Kontrol Kulesi"),
    STATUE(17, "Heykel"),
    BRIDGE_PIER(18, "Köprü Ayağı"),
    TOWER(19, "Kule"),
    LIGHTNING_ROD(20, "Paratoner"),
    RADIO_LINK_TOWER(21, "Radyo Link Kulesi"),
    RADIO_TV_LINE_POLE(22, "Radyo/TV Hattı Direği"),
    BILLBOARD(23, "Reklâm Panosu"),
    WIND_ENERGY_PLANT(24, "Rüzgâr Enerji Santrali"),
    WIND_MEASUREMENT_STATION(25, "Rüzgâr Ölçüm İstasyonu"),
    SILO(26, "Silo"),
    SWITCHYARD(27, "Şalt Sahası"),
    TELEFERIC_TELESEAT_POLE(28, "Teleferik/Telesiyej Hattı Direği"),
    TELEPHONE_TELEGRAPH_LINE_POLE(29, "Telefon/Telgraf Hattı Direği"),
    TV_TRANSMITTER(30, "TV Vericisi"),
    CRANE(31, "Vinç"),
    VIADUCT_PIER(32, "Viyadük Ayağı"),
    HISTORICAL_BUILDING(33, "Tarihi Yapı"),
    OTHER(999, "Diğer");

    private final int code;
    private final String description;

    StructureType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static StructureType getStructureTypeByCode(int code) {
        for (StructureType structureType : StructureType.values()) {
            if (structureType.getCode() == code) {
                return structureType;
            }
        }
        return StructureType.UNKNOWN; // Belirli bir kodla eşleşen bir yapı türü bulunamazsa, UNKNOWN döndürülür.
    }
}

