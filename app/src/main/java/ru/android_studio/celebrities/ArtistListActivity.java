package ru.android_studio.celebrities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
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
import ru.android_studio.celebrities.listener.OnListFragmentInteractionListener;
import ru.android_studio.celebrities.listener.OnLoadMoreListener;
import ru.android_studio.celebrities.model.db.ArtistDB;
import ru.android_studio.celebrities.model.dto.ArtistDTO;
import ru.android_studio.celebrities.retrofit_api.ArtistsAPI;

/*
* Главная активити со списком исполнителей
* из-за того что использую плагин Lombok нужно настроить среду разработки + добавить плагин
* Settings -> Compiler -> Annotation Processors
* */
public class ArtistListActivity extends AppCompatActivity implements OnListFragmentInteractionListener, Callback<ArtistDTO[]> {

    public static final String EXTRAS_ORDER_ID = "ORDER_ID";
    public static final String EXTRAS_ORDERS = "ORDERS";
    public static final int FIRST_LOAD_SIZE = 5;
    private static final String TAG = "ItemFragment";
    private static final String LAST_MODIFIED = "Last-Modified";
    private static List<ArtistDB> artistDBRealmResults;
    private static int maxSize;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.recycleView)

    RecyclerView mRecyclerView;

    ProgressDialog progressDialog;

    private List<ArtistDB> artistDBList = new LinkedList<>();

    private AuthorItemRecyclerViewAdapter adapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null) {
            progressDialog = ProgressDialog
                    .show(this,
                            getString(R.string.loading_title),
                            getString(R.string.loading_msg));
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_artist_list);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        // @TODO убрать хардкод
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://cache-default06e.cdn.yandex.net")
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        ArtistsAPI artistsAPI = retrofit.create(ArtistsAPI.class);
        if (isNeedUpdateDB(artistsAPI) && savedInstanceState == null) {
            Call<ArtistDTO[]> call = artistsAPI.loadArtists();
            call.enqueue(this);
        } else {
            loadArtistListFromDB(FIRST_LOAD_SIZE);
            if(progressDialog != null) {
                progressDialog.hide();
            }
        }


        adapter = new AuthorItemRecyclerViewAdapter();
        mRecyclerView.setAdapter(adapter);

        adapter.setOnListFragmentInteractionListener(this);
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e("haint", "Load More");
                artistDBList.add(null);
                adapter.notifyItemInserted(artistDBList.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");

                        //Remove loading item
                        artistDBList.remove(artistDBList.size() - 1);
                        adapter.notifyItemRemoved(artistDBList.size());

                        //Load data
                        loadArtistListFromDB(20);

                        adapter.notifyDataSetChanged();
                        adapter.setLoaded();
                    }
                }, 5000);
            }
        });

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    /*
    * Переход по клику в информацию об артисте
    * */
    @Override
    public void onListFragmentInteraction(ArtistDB artistDB) {
        Intent intent = new Intent(this, ArtistInfoActivity.class);
        intent.putExtra(EXTRAS_ORDER_ID, artistDB.getOrderId());

        ArrayList<Integer> arrayList = new ArrayList<>(artistDBRealmResults.size());

        for (ArtistDB artistDBRealmResult : artistDBRealmResults) {
            arrayList.add(artistDBRealmResult.getOrderId());
        }
        intent.putIntegerArrayListExtra(EXTRAS_ORDERS, arrayList);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        getRealm().close();
    }

    /*
    * При успешном соединении с сервером
    * Получение данных и сохранение в БД
    * */
    @Override
    public void onResponse(Call<ArtistDTO[]> call, Response<ArtistDTO[]> response) {
        writeLastModifiedDB(response);
        setProgressBarIndeterminateVisibility(false);

        List<ArtistDTO> artists = Arrays.asList(response.body());
        if (!artists.isEmpty()) {
            // Persist your data
            for (int i = 0; i < artists.size(); i++) {
                ArtistDTO artist = artists.get(i);
                getRealm().beginTransaction();
                ArtistDB artistDB = ArtistDB.of(artist, i);
                getRealm().copyToRealmOrUpdate(artistDB);
                artistDBList.add(artistDB);
                getRealm().commitTransaction();
            }
            Collections.sort(artistDBList);
        } else {
            loadArtistListFromDB(FIRST_LOAD_SIZE);
        }


        /*
        * Обновление данных на UI
        * */
        adapter.notifyDataSetChanged();
        if(progressDialog != null) {
            progressDialog.hide();
        }
    }

    public Realm getRealm() {
        if (realm == null || realm.isClosed()) {
            RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
            realm = Realm.getInstance(realmConfig);
        }
        return realm;
    }

    private void loadArtistListFromDB(int countItem) {
        if(artistDBRealmResults == null) {
            RealmQuery<ArtistDB> artistDBRealmQuery = getRealm().where(ArtistDB.class);
            Log.d(TAG, "Count of artists before persist: " + artistDBRealmQuery.count());

            artistDBRealmResults = artistDBRealmQuery.findAllSorted("orderId");
            maxSize = artistDBRealmResults.size();
        }

        int startIndex = artistDBList.size();
        int endIndex = startIndex + countItem;
        if (maxSize >= endIndex) {
            artistDBList.addAll(artistDBRealmResults.subList(startIndex, endIndex));
        } else {
            artistDBList.addAll(artistDBRealmResults.subList(startIndex, maxSize));
        }
    }

    private void writeLastModifiedDB(Response<ArtistDTO[]> response) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(LAST_MODIFIED, response.headers().get(LAST_MODIFIED));
        editor.apply();
    }

    /*
    * Обработка ошибки при получении данных с сервера
    *
    * При ошибке загружаем данные из БД
    * */
    @Override
    public void onFailure(Call<ArtistDTO[]> call, Throwable t) {
        RealmResults<ArtistDB> artistDBRealmResults = getRealm().where(ArtistDB.class).findAll();
        artistDBList.addAll(artistDBRealmResults);
        adapter.notifyDataSetChanged();
        progressDialog.hide();
    }

    /*
    * Проверяем нужно обновлять данные в БД или нет.
    * результат определяем по дате последнего обновления LAST_MODIFIED из заголовка
    * */
    private boolean isNeedUpdateDB(ArtistsAPI artistsAPI) {
        String lastModifiedDB = readLastModifiedDB(LAST_MODIFIED);
        if (lastModifiedDB != null) {
            String lastModifiedResponse = null;
            try {
                lastModifiedResponse = artistsAPI.getLoadArtistsHeader().execute().headers().get(LAST_MODIFIED);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (lastModifiedResponse != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                try {
                    Calendar calendarResponse = Calendar.getInstance();
                    calendarResponse.setTime(dateFormat.parse(lastModifiedResponse));

                    Calendar calendarDB = Calendar.getInstance();
                    calendarDB.setTime(dateFormat.parse(lastModifiedDB));

                    return calendarResponse.after(calendarDB);

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private String readLastModifiedDB(String key) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    class ArtistViewHolder extends RecyclerView.ViewHolder {

        View itemView;

        ImageView cover;
        TextView name;
        TextView genres;
        TextView tracks;
        TextView albums;
        ArtistDB item;

        public ArtistViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;

            name = (TextView) itemView.findViewById(R.id.name);
            genres = (TextView) itemView.findViewById(R.id.genres);
            tracks = (TextView) itemView.findViewById(R.id.tracks);
            albums = (TextView) itemView.findViewById(R.id.albums);
            cover = (ImageView) itemView.findViewById(R.id.cover);
        }
    }

    /*
    * Класс в котором происходит маппинг из ArtistDTO в ArtistViewHolder
    * */
    public class AuthorItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemTouchHelperAdapter {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;
        private OnListFragmentInteractionListener onListFragmentInteractionListener;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public AuthorItemRecyclerViewAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnListFragmentInteractionListener(OnListFragmentInteractionListener onListFragmentInteractionListener) {
            this.onListFragmentInteractionListener = onListFragmentInteractionListener;
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.onLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return artistDBList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }


        public void setLoaded() {
            isLoading = false;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(ArtistListActivity.this).inflate(R.layout.fragment_artist_item, parent, false);
                return new ArtistViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(ArtistListActivity.this).inflate(R.layout.layout_loading_item, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ArtistViewHolder) {
                final ArtistViewHolder artistViewHolder = (ArtistViewHolder) holder;
                ArtistDB artist = artistDBList.get(position);
                artistViewHolder.name.setText(artist.getName());
                artistViewHolder.tracks.setText(artist.getTraksText(getApplicationContext()));
                artistViewHolder.genres.setText(artist.getGenreList());
                artistViewHolder.albums.setText(artist.getAlbumsText(getApplicationContext()));
                artistViewHolder.item = artist;

                ImageView imageView = artistViewHolder.cover;
                String url = artist.getCover().getSmall();
                ImageLoader.loadByUrlToImageView(getApplicationContext(), url, imageView);

                artistViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onListFragmentInteractionListener != null && artistViewHolder.item != null) {
                            onListFragmentInteractionListener.onListFragmentInteraction(artistViewHolder.item);
                        }
                    }
                });

            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }
        }

        @Override
        public int getItemCount() {
            return artistDBList.size();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(artistDBList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(artistDBList, i, i - 1);
                }
            }
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(int position) {
            ArtistDB artistDB = artistDBList.get(position);
            ArtistDB resultArtistDB = getRealm().where(ArtistDB.class).equalTo("id", artistDB.getId()).findFirst();

            Number maxOrderId = getRealm().where(ArtistDB.class).max("orderId");
            getRealm().beginTransaction();
            if (maxOrderId == null) {
                resultArtistDB.setOrderId(1);
            } else {
                resultArtistDB.setOrderId(maxOrderId.intValue() + 1);
            }

            getRealm().copyToRealmOrUpdate(resultArtistDB);
            getRealm().commitTransaction();

            artistDBList.remove(position);
            artistDBList.add(resultArtistDB);

            notifyItemRemoved(position);
            notifyItemInserted(artistDBList.size() - 1);
            notifyDataSetChanged();
        }
    }
}