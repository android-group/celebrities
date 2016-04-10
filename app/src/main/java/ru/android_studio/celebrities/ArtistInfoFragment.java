package ru.android_studio.celebrities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.android_studio.celebrities.model.db.ArtistDB;

public class ArtistInfoFragment extends Fragment {

    private ArtistDB currentArtist;
    private Realm realm;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.heart)
    FloatingActionButton heart;

    @Bind(R.id.description)
    TextView descriptionTV;

    @Bind(R.id.albums)
    TextView albumsTV;

    @Bind(R.id.tracks)
    TextView tracksTV;

    @Bind(R.id.header_cover)
    ImageView imageView;

    private int artistId;

    public static final String TAG = "ArtistInfoFragment";

    public ArtistInfoFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
        realm = Realm.getInstance(realmConfig);

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artist_info, container, false);
        ButterKnife.bind(this, view);

        if(savedInstanceState != null) {
            artistId = savedInstanceState.getInt("artistId");
        } else {
            artistId = getArguments().getInt("artistId");
        }
        loadByArtistId();

        return view;
    }

    private void loadByOrderId(Integer orderId) {
        Log.i(TAG, "loadByOrderId artist");
        if (orderId == null) {
            return;
        }

        ArtistDB found = realm.where(ArtistDB.class).equalTo("orderId", orderId).findFirst();
        load(found);
    }

    private void loadByArtistId() {
        Log.i(TAG, "loadByArtistId artist");

        ArtistDB found = realm.where(ArtistDB.class).equalTo("id", artistId).findFirst();
        load(found);
    }

    private void load(ArtistDB artistDB) {
        Log.i(TAG, "load artist");
        if (artistDB == null) {
            return;
        }
        currentArtist = artistDB;

        String url = currentArtist.getCover().getBig();
        ImageLoader.loadByUrlToImageView(getContext(), url, imageView);

        Log.i(TAG, "Set current artist name: " + currentArtist.getName());
        toolbar.setTitle(currentArtist.getName());
        Log.i(TAG, "Set current artist name: " + currentArtist.getDescription());
        descriptionTV.setText(currentArtist.getDescription());

        albumsTV.setText(currentArtist.getAlbumsText(getContext()));
        tracksTV.setText(currentArtist.getTraksText(getContext()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }

}
