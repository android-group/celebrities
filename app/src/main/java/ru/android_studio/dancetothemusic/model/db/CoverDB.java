package ru.android_studio.dancetothemusic.model.db;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.android_studio.dancetothemusic.model.dto.CoverDTO;

@ToString
@EqualsAndHashCode(callSuper = false)
public class CoverDB extends RealmObject {

    @Getter
    @Setter
    private String small;

    @Getter
    @Setter
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