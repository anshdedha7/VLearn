package com.mypocketstore.seasons.sync;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;

import com.mypocketstore.seasons.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by anshdedha7 on 10/11/15.
 */
public class FetchEpisodeImageTask extends AsyncTask<String, Void, Bitmap> {

    Context mContext;
    ImageView mImageView;
    int mImageDoesNotExistFlag = 0;
    View mProgressBarView;

    public FetchEpisodeImageTask(Context c, ImageView v, View pv){
        mContext = c;
        mImageView = v;
        mProgressBarView = pv;
    }

    private String getBaseUrl(){
        SharedPreferences sp = mContext.getSharedPreferences("main", Context.MODE_PRIVATE);
        return  sp.getString("base_url", null);
    }

    public void saveImageToDirectory(Bitmap bitmap, String filename){
        FileOutputStream fos = null;
        ContextWrapper cw = new ContextWrapper(mContext);
        File directory = cw.getDir("EpisodeDir", Context.MODE_PRIVATE);
        File mypath = new File(directory,filename);

        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String path = params[0];
        String string_url = getBaseUrl() + "w500" + path;

        if (path.equals("null")){
            mImageDoesNotExistFlag = 1;
        }else {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(string_url).getContent());
                saveImageToDirectory(bitmap, path);
                return bitmap;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mProgressBarView.setVisibility(View.GONE);
        if (mImageDoesNotExistFlag == 0) {
            mImageView.setImageBitmap(bitmap);
        }
    }
}
