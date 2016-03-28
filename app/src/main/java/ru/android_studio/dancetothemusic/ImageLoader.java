package ru.android_studio.dancetothemusic;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ImageLoader {

    private static final String TAG = "ImageLoader";
    Context context;

    public ImageLoader(Context context) {
        this.context = context;
    }

    public void loadCover(String urlCover, ImageView cover) {
        new DownloadCoverTask(cover).doInBackground(urlCover);
    }

    public class DownloadCoverTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public DownloadCoverTask(ImageView imageView) {
            this.imageView = imageView;
        }

        protected void onPreExecute() {
        }

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
                URLConnection urlConnection = (URLConnection) url.getContent();
                InputStream inputStream = urlConnection.getInputStream();

                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (IOException e) {
                Log.e(TAG, "image can't be download");
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result) {
            //set image of your imageview
            imageView.setImageBitmap(result);
        }
    }
}