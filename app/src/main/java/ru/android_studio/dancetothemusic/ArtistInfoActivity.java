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
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;

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

    ArtistDTO currentArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_info);

        // initialization http://jakewharton.github.io/butterknife/
        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentArtist = (ArtistDTO) extras.get("ARTIST_DTO");
                if (currentArtist != null) {

                    String url = currentArtist.getCover().getBig();
                    ImageLoader.load(getApplicationContext(), url, imageView);

                    toolbar.setTitle(currentArtist.getName());
                    descriptionTV.setText(currentArtist.getDescription());
                    albumsTV.setText(currentArtist.getAlbumsText(this));
                    tracksTV.setText(currentArtist.getTraksText(this));
                }
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
}