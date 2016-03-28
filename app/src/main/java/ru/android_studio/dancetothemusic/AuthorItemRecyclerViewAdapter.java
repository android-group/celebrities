package ru.android_studio.dancetothemusic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.android_studio.dancetothemusic.ItemFragment.OnListFragmentInteractionListener;
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;

public class AuthorItemRecyclerViewAdapter extends RecyclerView.Adapter<AuthorItemRecyclerViewAdapter.ViewHolder> {

    private final static String TAG = "AUTHOR_ITEM_RECYCLER_VIEW_ADAPTER";
    private final List<ArtistDTO> items;
    private final OnListFragmentInteractionListener listener;
    public ImageLoader imageLoader;

    public AuthorItemRecyclerViewAdapter(FragmentActivity activity, List<ArtistDTO> items, OnListFragmentInteractionListener listener) {
        this.items = items;
        this.listener = listener;
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ArtistDTO artistDTO = items.get(position);
        if (artistDTO == null) {
            System.out.println("artistDTO is null on position: " + position);
            return;
        }

        holder.setItem(artistDTO);

        imageLoader.loadCover(artistDTO.getCover().getSmall(), holder.getCover());

        holder.getName().setText(artistDTO.getName());
        holder.getTracks().setText("" + artistDTO.getTracks());
        holder.getGenres().setText(Arrays.toString(artistDTO.getGenres()));
        holder.getAlbums().setText("" + artistDTO.getAlbums());

        holder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onListFragmentInteraction(holder.getItem());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    void loadCover(ImageView cover, String coverLink) throws IOException {
        cover.setImageBitmap(new DownloadCoverTask().doInBackground(coverLink));
    }

    @ToString
    @EqualsAndHashCode(callSuper = false)
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final ImageView cover;
        private final TextView name;
        private final TextView genres;
        private final TextView tracks;
        private final TextView albums;
        private ArtistDTO item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;

            this.cover = (ImageView) view.findViewById(R.id.cover);
            this.name = (TextView) view.findViewById(R.id.name);
            this.genres = (TextView) view.findViewById(R.id.genres);
            this.tracks = (TextView) view.findViewById(R.id.tracks);
            this.albums = (TextView) view.findViewById(R.id.albums);
        }


        public View getView() {
            return view;
        }

        public ImageView getCover() {
            return cover;
        }

        public TextView getName() {
            return name;
        }

        public TextView getGenres() {
            return genres;
        }

        public TextView getTracks() {
            return tracks;
        }

        public TextView getAlbums() {
            return albums;
        }

        public ArtistDTO getItem() {
            return item;
        }

        public void setItem(ArtistDTO item) {
            this.item = item;
        }
    }

    public class DownloadCoverTask extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            if (params.length == 0) {
                System.err.println("params can't be null");
                return null;
            }

            String urlCover = params[0];
            Bitmap bitmap = null;
            try {
                URL url = new URL(urlCover);
                InputStream inputStream = (InputStream) url.getContent();

                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "image can't be download");
            }
            return bitmap;
        }
    }
}
