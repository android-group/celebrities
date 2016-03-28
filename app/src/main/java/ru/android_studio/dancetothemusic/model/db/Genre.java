package ru.android_studio.dancetothemusic.model.db;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode(callSuper = false)
public class Genre extends RealmObject {

    @Getter
    @Setter
    String name;

    public Genre(String name) {
        this.name = name;
    }

    public Genre() {
    }
}
