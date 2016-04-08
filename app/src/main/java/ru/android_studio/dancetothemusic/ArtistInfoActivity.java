package ru.android_studio.dancetothemusic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.android_studio.dancetothemusic.model.db.ArtistDB;

/*
* Активити с информацией об исполнителе
* */
public class ArtistInfoActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton emailICO;

    @Bind(R.id.description)
    TextView descriptionTV;

    @Bind(R.id.albums)
    TextView albumsTV;

    @Bind(R.id.tracks)
    TextView tracksTV;

    @Bind(R.id.header_cover)
    ImageView imageView;

    ArtistDB currentArtist;

    private Realm realm;

    private Integer artistId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        // initialization http://jakewharton.github.io/butterknife/
        ButterKnife.bind(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getApplicationContext()).build();
        realm = Realm.getInstance(realmConfig);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                artistId = (Integer) extras.get(ArtistListActivity.EXTRAS_ARTIST_ID);
                loadByArtistId();
            }
        }

        setSupportActionBar(toolbar);
        emailICO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ArtistListActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadByArtistId() {
        if(artistId == null) {
            return;
        }

        currentArtist = realm.where(ArtistDB.class).equalTo("id", artistId).findFirst();
        if (currentArtist == null) {
            return;
        }

        String url = currentArtist.getCover().getBig();
        ImageLoader.loadByUrlToImageView(getApplicationContext(), url, imageView);

        toolbar.setTitle(currentArtist.getName());
        descriptionTV.setText(currentArtist.getDescription());
        albumsTV.setText(currentArtist.getAlbumsText(this));
        tracksTV.setText(currentArtist.getTraksText(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        // Save UI state changes to the savedInstanceState.
        // This bundle will be passed to onCreate if the process is
        // killed and restarted.
        savedInstanceState.putInt(ArtistListActivity.EXTRAS_ARTIST_ID, artistId);
        // etc.
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.
        artistId = savedInstanceState.getInt(ArtistListActivity.EXTRAS_ARTIST_ID);
        loadByArtistId();
    }
}