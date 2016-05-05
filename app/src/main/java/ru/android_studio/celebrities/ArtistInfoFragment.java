package ru.android_studio.celebrities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.android_studio.celebrities.model.db.ArtistDB;

public class ArtistInfoFragment extends Fragment {

    private ArtistDB currentArtist;

    private Realm realm;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    //@BindView(R.id.heart)
    //FloatingActionButton heart;

    @BindView(R.id.description)
    TextView descriptionTV;

    @BindView(R.id.albums)
    TextView albumsTV;

    @BindView(R.id.tracks)
    TextView tracksTV;

    @BindView(R.id.header_cover)
    ImageView imageView;

    private int orderId;

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
            orderId = savedInstanceState.getInt(ArtistListActivity.EXTRAS_ORDER_ID);
        } else {
            orderId = getArguments().getInt(ArtistListActivity.EXTRAS_ORDER_ID);
        }
        Log.i(TAG, "loadByOrderId orderId: " + orderId);
        loadByOrderId();

        return view;
    }

    private void loadByOrderId() {
        ArtistDB found = realm.where(ArtistDB.class).equalTo("orderId", orderId).findFirst();
        load(found);
    }

    private void load(ArtistDB artistDB) {
        if (artistDB == null) {
            return;
        }
        currentArtist = artistDB;

        String url = currentArtist.getCover().getBig();
        ImageLoader.loadByUrlToImageView(getContext(), url, imageView);

        Log.i(TAG, "Set current artist name: " + currentArtist.getName());
        toolbar.setTitle(getString(R.string.celebrities) + ": " + currentArtist.getName());
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
