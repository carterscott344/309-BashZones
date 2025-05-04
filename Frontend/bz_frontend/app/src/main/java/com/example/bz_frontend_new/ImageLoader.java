package com.example.bz_frontend_new;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class ImageLoader extends android.os.AsyncTask<byte[], Void, Bitmap> {
    private final ImageView imageView;

    public ImageLoader(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    protected Bitmap doInBackground(byte[]... data) {
        if (data == null || data.length == 0 || data[0] == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(data[0], 0, data[0].length);
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (result != null) {
            imageView.setImageBitmap(result);
        }
    }
}
