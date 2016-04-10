package ru.android_studio.celebrities.model.db;

import io.realm.RealmObject;
import lombok.Data;
import ru.android_studio.celebrities.model.dto.CoverDTO;

@Data
public class CoverDB extends RealmObject {

    private String small;
    private String big;

    public CoverDB() {

    }

    public static CoverDB of(CoverDTO coverDTO) {
        CoverDB result = new CoverDB();
        result.setBig(coverDTO.getBig());
        result.setSmall(coverDTO.getSmall());
        return result;
    }
}