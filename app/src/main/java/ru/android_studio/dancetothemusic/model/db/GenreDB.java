package ru.android_studio.dancetothemusic.model.db;

import io.realm.RealmObject;
import lombok.Data;

@Data
public class GenreDB extends RealmObject {

    String name;

    public GenreDB(String name) {
        this.name = name;
    }

    public GenreDB() {
    }

    @Override
    public String toString() {
        return name;
    }
}
