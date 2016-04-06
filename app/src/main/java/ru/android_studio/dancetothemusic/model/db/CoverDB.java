package ru.android_studio.dancetothemusic.model.db;

import java.io.Serializable;

import io.realm.RealmObject;
import lombok.Data;
import ru.android_studio.dancetothemusic.model.dto.CoverDTO;

@Data
public class CoverDB extends RealmObject {

    private String small;
    private String big;

    public CoverDB() {

    }

    public static CoverDB of(CoverDTO coverDTO) {
        CoverDB result = new CoverDB();
        result.setBig(coverDTO.getBig());
        result.setBig(coverDTO.getSmall());
        return result;
    }
}