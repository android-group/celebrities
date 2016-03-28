package ru.android_studio.dancetothemusic.model.db;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
}