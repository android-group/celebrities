package ru.android_studio.dancetothemusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.dancetothemusic.dummy.DummyContent;
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;
import ru.android_studio.dancetothemusic.retrofit_api.ArtistsAPI;
import ru.android_studio.dancetothemusic.model.db.ArtistDB;

public class ArtistListActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener, Callback<ArtistDTO[]> {

    private static final String TAG = "ArtistListActivity";
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;
    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_list);

        // initialization http://jakewharton.github.io/butterknife/
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://cache-default06e.cdn.yandex.net")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();


        // prepare call in Retrofit 2.0
        ArtistsAPI artistsAPI = retrofit.create(ArtistsAPI.class);
        Call<ArtistDTO[]> call = artistsAPI.loadArtists();

        call.enqueue(this);

        // Create a RealmConfiguration which is to locate Realm file in package's "files" directory.
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        // Get a Realm instance for this thread
        realm = Realm.getInstance(realmConfig);

    }

    @OnClick(R.id.fab)
    void play() {
        Toast.makeText(this, "Hello, views!", Toast.LENGTH_SHORT).show();
    }

    /*
    * Переход по клику в информацию об артисте
    * */
    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Intent intent = new Intent(this, ArtistInfoActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist_info, menu);
        return true;
    }

    /*
    * Получение данных и сохранение его в БД при успешном соединении с сервером
    * */
    @Override
    public void onResponse(Call<ArtistDTO[]> call, Response<ArtistDTO[]> response) {
        setProgressBarIndeterminateVisibility(false);

        RealmResults<ArtistDB> artistsRealm = realm.where(ArtistDB.class).findAll();
        Log.i(TAG, "Count of artists before persist: " + artistsRealm.size());

        ArtistDTO[] artists = response.body();
        for (ArtistDTO artist : artists) {
            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(ArtistDB.of(artist));
            realm.commitTransaction();
        }
        Log.i(TAG, "Count of artists before persist: " + artistsRealm.size());
    }

    /*
    * Обработка ошибки при получении данных с сервера
    * */
    @Override
    public void onFailure(Call<ArtistDTO[]> call, Throwable t) {
        // nothing to show for user ;)
    }
}