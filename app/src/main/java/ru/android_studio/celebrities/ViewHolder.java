package ru.android_studio.celebrities;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.android_studio.celebrities.model.db.ArtistDB;

@ToString
@EqualsAndHashCode(callSuper = false)
public class ViewHolder extends RecyclerView.ViewHolder {

    private final View view;

    private final ImageView cover;
    private final TextView name;
    private final TextView genres;
    private final TextView tracks;
    private final TextView albums;
    private ArtistDB item;

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

    public ArtistDB getItem() {
        return item;
    }

    public void setItem(ArtistDB item) {
        this.item = item;
    }

    public void bind(ArtistDB artistDB){
        setItem(artistDB);
        getName().setText(artistDB.getName());
        getTracks().setText(artistDB.getTraksText(view.getContext()));
        getGenres().setText(artistDB.getGenreList());
        getAlbums().setText(artistDB.getAlbumsText(view.getContext()));

        ImageView imageView = getCover();
        String url = artistDB.getCover().getSmall();
        ImageLoader.loadByUrlToImageView(view.getContext(), url, imageView);
    }
}
