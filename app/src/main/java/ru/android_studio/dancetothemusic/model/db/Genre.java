package ru.android_studio.dancetothemusic.model.db;

import io.realm.RealmObject;
import lombok.Data;

@Data
public class Genre extends RealmObject {

    String name;

    public Genre(String name) {
        this.name = name;
    }

    public Genre() {
    }
}
