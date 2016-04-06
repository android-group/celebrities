package ru.android_studio.dancetothemusic.model.dto;

import lombok.Data;

@Data
public class ArtistDTO {

    private Integer id;
    private String name;
    private String[] genres;
    private Integer tracks;
    private Integer albums;
    private String link;
    private String description;
    private CoverDTO cover;

    public ArtistDTO(Integer id, String name, String[] genres, Integer tracks, Integer albums, String link, String description, CoverDTO cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    public ArtistDTO() {

    }
}