package ru.android_studio.dancetothemusic;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.android_studio.dancetothemusic.model.db.ArtistDB;

/*
* Главная активити со списком исполнителей
* из-за того что использую плагин Lombok нужно настроить среду разработки + добавить плагин
* Settings -> Compiler -> Annotation Processors
* */
public class ArtistListActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {

    public static final String EXTRAS_ARTIST_ID = "ARTIST_ID";

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fab)
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_artist_list);

        // initialization http://jakewharton.github.io/butterknife/
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

    }

    @OnClick(R.id.fab)
    void play() {
        Toast.makeText(this, "Hello, views!", Toast.LENGTH_SHORT).show();
    }

    /*
    * Переход по клику в информацию об артисте
    * */
    @Override
    public void onListFragmentInteraction(ArtistDB artistDB) {
        Intent intent = new Intent(this, ArtistInfoActivity.class);
        intent.putExtra(EXTRAS_ARTIST_ID, artistDB.getId());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist_info, menu);
        return true;
    }
}