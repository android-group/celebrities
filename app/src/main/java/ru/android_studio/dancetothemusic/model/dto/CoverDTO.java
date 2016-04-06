package ru.android_studio.dancetothemusic.model.dto;

import java.io.Serializable;

import lombok.Data;
import ru.android_studio.dancetothemusic.model.db.CoverDB;

@Data
public class CoverDTO {

    private String small;

    private String big;

    public CoverDTO() {

    }

    public static CoverDTO of(CoverDB coverDB) {
        CoverDTO result = new CoverDTO();
        result.big = coverDB.getBig();
        result.small = coverDB.getSmall();
        return result;
    }
}