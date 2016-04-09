package ru.android_studio.dancetothemusic.model.db;

import android.app.Activity;
import android.support.annotation.NonNull;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import lombok.Data;
import ru.android_studio.dancetothemusic.R;
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;

@Data
public class ArtistDB extends RealmObject implements Comparable<ArtistDB> {

    @PrimaryKey
    private Integer id;

    private String name;
    private RealmList<GenreDB> genres;
    private Integer tracks;
    private Integer albums;
    private String link;
    private String description;
    private CoverDB cover;
    private Integer orderId;

    public ArtistDB() {

    }

    public ArtistDB(Integer id, String name, RealmList<GenreDB> genres, Integer tracks, Integer albums, String link, String description, CoverDB cover, Integer orderId) {
        this.id = id;
        this.name = name;
        this.genres = genres;
        this.tracks = tracks;
        this.albums = albums;
        this.link = link;
        this.description = description;
        this.cover = cover;
        this.orderId = orderId;
    }

    public static ArtistDB of(ArtistDTO artistDTO, Integer orderId) {
        if (artistDTO == null) {
            throw new IllegalArgumentException("Argument artistDTO can't be null");
        }

        RealmList<GenreDB> genreRealmList = new RealmList<>();
        for (String genresDTO : artistDTO.getGenres()) {
            genreRealmList.add(new GenreDB(genresDTO));
        }

        CoverDB coverDB = CoverDB.of(artistDTO.getCover());

        return new ArtistDB(artistDTO.getId(), artistDTO.getName(), genreRealmList, artistDTO.getTracks(), artistDTO.getAlbums(), artistDTO.getLink(), artistDTO.getDescription(), coverDB, orderId);
    }

    public Integer getOrderId() {
        return orderId;
    }

    @Override
    public int compareTo(@NonNull ArtistDB another) {
        int GREATER_THEN_ANOTHER = 1;
        int LESS_THEN_ANOTHER = -1;

        if (another.getOrderId() == null) {
            if (another.getName() == null) {
                return GREATER_THEN_ANOTHER;
            }

            if (this.name == null) {
                return LESS_THEN_ANOTHER;
            }
            return this.name.compareTo(another.getName());
        }

        if (this.orderId == null) {
            return LESS_THEN_ANOTHER;
        }

        return this.orderId.compareTo(another.getOrderId());
    }

    public String getGenreList() {
        StringBuilder builder = new StringBuilder();
        for (GenreDB genre : genres) {
            builder.append(genre.getName());
            builder.append(", ");
        }
        if(builder.length() > 0) {
            builder.delete(builder.length() - 2, builder.length());
        }
        return builder.toString();
    }

    public String getTraksText(Activity activity) {
        return String.format("%d %s", getTracks(), activity.getString(R.string.tracks_info));
    }

    public String getAlbumsText(Activity activity) {
        return String.format("%d %s", getAlbums(), activity.getString(R.string.albums_info));
    }
}