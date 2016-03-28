package ru.android_studio.dancetothemusic.model.dto;

import io.realm.annotations.PrimaryKey;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ArtistDTO {

    @PrimaryKey
    @Getter
    @Setter
    private Integer id;

    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String[] genres;

    @Getter
    @Setter
    private Integer tracks;

    @Getter
    @Setter
    private Integer albums;

    @Getter
    @Setter
    private String link;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private CoverDTO cover;

    public ArtistDTO() {
    }
}