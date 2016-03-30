package ru.android_studio.dancetothemusic.model.dto;

import java.io.Serializable;

import io.realm.RealmObject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class CoverDTO implements Serializable {

    @Getter
    @Setter
    private String small;

    @Getter
    @Setter
    private String big;

    public CoverDTO() {

    }
}