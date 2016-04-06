package ru.android_studio.dancetothemusic;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
 * Фрагмент со списком исполнителей.
 *
 * В этом классе происходит загрузка RecyclerView
 * получени json с сервер через интернет (с помощью Retrofit),
 * но если не получается,
 * тогда загружаем список из бд sql lite (с помощью RealmDB)
 */
public class ItemFragment extends Fragment implements Callback<ArtistDTO[]> {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String TAG = "ItemFragment";

    private int mColumnCount = 1;

    private OnListFragmentInteractionListener onListFragmentInteractionListener;
    private List<ArtistDB> artistDTOList = new ArrayList<>();
    private AuthorItemRecyclerViewAdapter adapter;

    private Realm realm;

    public ItemFragment() {
    }

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

        // @TODO убрать хардкод
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://cache-default06e.cdn.yandex.net")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ArtistsAPI artistsAPI = retrofit.create(ArtistsAPI.class);
        Call<ArtistDTO[]> call = artistsAPI.loadArtists();
        call.enqueue(this);

        RealmConfiguration realmConfig = new RealmConfiguration.Builder(getContext()).build();
        realm = Realm.getInstance(realmConfig);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            adapter = new AuthorItemRecyclerViewAdapter(getActivity(), artistDTOList, onListFragmentInteractionListener, realm);
            recyclerView.setAdapter(adapter);

            ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        return view;
    }

    /**
     * Важно context должен реализовывать интерфейс OnListFragmentInteractionListener
     * мы должны обрабатываем клики по списку
     *
     * @throws RuntimeException - unchecked exceptions
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            onListFragmentInteractionListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onListFragmentInteractionListener = null;
    }

    /*
    * При успешном соединении с сервером
    * Получение данных и сохранение в БД
    * */
    @Override
    public void onResponse(Call<ArtistDTO[]> call, Response<ArtistDTO[]> response) {
        getActivity().setProgressBarIndeterminateVisibility(false);

        RealmQuery<ArtistDB> artistDBRealmQuery = realm.where(ArtistDB.class);
        Log.d(TAG, "Count of artists before persist: " + artistDBRealmQuery.count());

        List<ArtistDTO> artists = Arrays.asList(response.body());
        if (!artists.isEmpty()) {
            // Persist your data
            for (int i = 0; i < artists.size(); i++) {
                ArtistDTO artist = artists.get(i);
                realm.beginTransaction();
                ArtistDB artistDB = ArtistDB.of(artist, i);
                realm.copyToRealmOrUpdate(artistDB);
                artistDTOList.add(artistDB);
                realm.commitTransaction();
            }
        } else {
            artistDTOList.addAll(artistDBRealmQuery.findAllSorted("orderId"));
        }
        Collections.sort(artistDTOList);

        /*
        * Обновление данных на UI
        * */
        adapter.notifyDataSetChanged();
    }

    /*
    * Обработка ошибки при получении данных с сервера
    *
    * При ошибке загружаем данные из БД
    * */
    @Override
    public void onFailure(Call<ArtistDTO[]> call, Throwable t) {
        RealmResults<ArtistDB> artistDBRealmResults = realm.where(ArtistDB.class).findAll();
        artistDTOList.addAll(artistDBRealmResults);
        adapter.notifyDataSetChanged();
    }

    /*
    * Листнер обрабатывает клик по строчке
    * */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ArtistDB item);
    }
}
