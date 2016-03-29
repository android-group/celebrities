package ru.android_studio.dancetothemusic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import ru.android_studio.dancetothemusic.model.db.ArtistDB;
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;
import ru.android_studio.dancetothemusic.retrofit_api.ArtistsAPI;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ItemFragment extends Fragment implements Callback<ArtistDTO[]> {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "ItemFragment";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private Realm realm;
    private List<ArtistDTO> artistDTOList;
    private RecyclerView recyclerView;
    private AuthorItemRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ItemFragment newInstance(int columnCount) {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
        realm = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

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
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
        // Get a Realm instance for this thread
        realm = Realm.getInstance(realmConfig);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            artistDTOList = new ArrayList<>();
            adapter = new AuthorItemRecyclerViewAdapter(getActivity(), artistDTOList, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /*
    * При успешном соединении с сервером
    * Получение данных и сохранение в БД
    * */
    @Override
    public void onResponse(Call<ArtistDTO[]> call, Response<ArtistDTO[]> response) {
        getActivity().setProgressBarIndeterminateVisibility(false);

        RealmQuery<ArtistDB> artistDB = realm.where(ArtistDB.class);
        Log.d(TAG, "Count of artists before persist: " + artistDB.count());

        ArtistDTO[] artists = response.body();

        /*
        @TODO Перенести в асинхронный процесс
        for (ArtistDTO artist : artists) {
            // Persist your data easily
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(ArtistDB.of(artist));
            realm.commitTransaction();
        }*/

        /*
        * @TODO На форму добавить только первые 25, а дальше сделать паджинациию
        * */
        artistDTOList.addAll(Arrays.asList(artists));

        /*
        * Обновление данных на UI
        * */
        adapter.notifyDataSetChanged();

        Log.d(TAG, "Count of artists before persist: " + artistDB.count());
    }

    /*
    * Обработка ошибки при получении данных с сервера
    *
    * При ошибке загружаем данные из БД
    * */
    @Override
    public void onFailure(Call<ArtistDTO[]> call, Throwable t) {
        RealmResults<ArtistDB> artistDBRealmResults = realm.where(ArtistDB.class).findAll();
        for (ArtistDB artistDB : artistDBRealmResults) {
            artistDTOList.add(ArtistDTO.of(artistDB));
        }
        adapter.notifyDataSetChanged();
    }

    /*
    * Листнер обрабатывает клик по строчке
    * */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ArtistDTO item);
    }
}
