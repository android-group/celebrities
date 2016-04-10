package ru.android_studio.celebrities.retrofit_api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HEAD;
import ru.android_studio.celebrities.model.dto.ArtistDTO;

/*
* В этом классе описаны способы взаимодействия с
* "http://cache-default06e.cdn.yandex.net"
* ItemFragment (line 87)
* */
public interface ArtistsAPI {

    @GET("/download.cdn.yandex.net/mobilization-2016/artists.json")
    Call<ArtistDTO[]> loadArtists();

    @HEAD("/download.cdn.yandex.net/mobilization-2016/artists.json")
    Call<Void> getLoadArtistsHeader();
}
