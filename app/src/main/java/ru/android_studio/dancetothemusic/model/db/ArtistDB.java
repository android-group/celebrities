package ru.android_studio.dancetothemusic.model.db;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;

@Data
public class ArtistDB extends RealmObject {

    @PrimaryKey
    private Integer id;

    private String name;
    private RealmList<Genre> genres;
    private Integer tracks;
    private Integer albums;
    private String link;
    private String description;
    private CoverDB cover;

    public ArtistDB() {

    }

    public ArtistDB(Integer id, String name, RealmList<Genre> genres, Integer tracks, Integer albums, String link, String description, CoverDB cover) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
    }

    public static ArtistDB of(ArtistDTO artistDTO) {
        if (artistDTO == null) {
            throw new IllegalArgumentException("Argument artistDTO can't be null");
        }

        RealmList<Genre> genreRealmList = new RealmList<>();
        for (String genresDTO : artistDTO.getGenres()) {
            genreRealmList.add(new Genre(genresDTO));
        }

        CoverDB coverDB = CoverDB.of(artistDTO.getCover());

        return new ArtistDB(artistDTO.getId(), artistDTO.getName(), genreRealmList, artistDTO.getTracks(), artistDTO.getAlbums(), artistDTO.getLink(), artistDTO.getDescription(), coverDB);
    }
}