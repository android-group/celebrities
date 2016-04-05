package ru.android_studio.dancetothemusic.model.dto;

import android.app.Activity;

import java.io.Serializable;

import io.realm.RealmList;
import lombok.Data;
import ru.android_studio.dancetothemusic.R;
import ru.android_studio.dancetothemusic.model.db.ArtistDB;
import ru.android_studio.dancetothemusic.model.db.Genre;

@Data
public class ArtistDTO implements Serializable {

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

    public static ArtistDTO of(ArtistDB artistDB) {
        RealmList<Genre> genresDB = artistDB.getGenres();
        String[] genres = new String[genresDB.size()];
        for (int i = 0; i < genresDB.size(); i++) {
            Genre genre = genresDB.get(i);
            if (genre != null) {
                genres[i] = genre.getName();
            }
        }

        return new ArtistDTO(
                artistDB.getId(),
                artistDB.getName(),
                genres,
                artistDB.getTracks(),
                artistDB.getAlbums(),
                artistDB.getLink(),
                artistDB.getDescription(),
                CoverDTO.of(artistDB.getCover())
        );
    }

    public String getGenreList() {
        StringBuilder builder = new StringBuilder();
        for (String genre : genres) {
            builder.append(genre);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder.toString();
    }

    public String getTraksText(Activity activity) {
        return String.format("%d %s", getTracks(), activity.getString(R.string.tracks_info));
    }

    public String getAlbumsText(Activity activity) {
        return String.format("%d %s", getAlbums(), activity.getString(R.string.albums_info));
    }
}