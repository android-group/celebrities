package ru.android_studio.dancetothemusic;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.android_studio.dancetothemusic.ItemFragment.OnListFragmentInteractionListener;
import ru.android_studio.dancetothemusic.model.db.ArtistDB;
import ru.android_studio.dancetothemusic.model.dto.ArtistDTO;

/*
* Класс в котором происходит маппинг из ArtistDTO в ViewHolder
* */
public class AuthorItemRecyclerViewAdapter extends RecyclerView.Adapter<AuthorItemRecyclerViewAdapter.ViewHolder> implements ItemTouchHelperAdapter {

    private final List<ArtistDTO> items;
    private final OnListFragmentInteractionListener listener;
    private final Realm realm;
    public FragmentActivity activity;

    public AuthorItemRecyclerViewAdapter(FragmentActivity activity, List<ArtistDTO> items, OnListFragmentInteractionListener listener, Realm realm) {
        this.activity = activity;
        this.items = items;
        this.listener = listener;
        this.realm = realm;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ArtistDTO artistDTO = items.get(position);
        if (artistDTO == null) {
            System.out.println("artistDTO is null on position: " + position);
            return;
        }

        holder.setItem(artistDTO);

        ImageView imageView = holder.getCover();
        String url = artistDTO.getCover().getSmall();
        ImageLoader.load(activity, url, imageView);

        holder.getName().setText(artistDTO.getName());
        holder.getTracks().setText(artistDTO.getTraksText(activity));
        holder.getGenres().setText(artistDTO.getGenreList());
        holder.getAlbums().setText(artistDTO.getAlbumsText(activity));

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

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(items, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(items, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        ArtistDTO artistDTO = items.get(position);
        RealmResults<ArtistDB> results = realm.where(ArtistDB.class).equalTo("id", artistDTO.getId()).findAll();

        realm.beginTransaction();
        results.clear();
        realm.commitTransaction();

        items.remove(position);
        notifyItemRemoved(position);
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
}
