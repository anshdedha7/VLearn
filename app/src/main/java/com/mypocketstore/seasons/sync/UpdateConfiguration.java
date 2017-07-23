package com.mypocketstore.seasons.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by anshdedha7 on 05/11/15.
 */
public class UpdateConfiguration extends AsyncTask<Void, Void, String> {

    Context mContext;

    public UpdateConfiguration(Context c){
        mContext = c;
    }

    private String parseJson(String rawStr) throws JSONException {
        try{
            JSONObject parentObject = new JSONObject(rawStr);
            JSONObject images = parentObject.getJSONObject("images");
            String base_url = images.getString("base_url");
            return base_url;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected String doInBackground(Void... params) {
        String string_url = "http://api.themoviedb.org/3/configuration?api_key=7210257ce98ebcdcc9e4daa7aa235114";
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(string_url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            String string_json = buffer.toString();
            Log.d("MY TAG", string_json);
            return parseJson(string_json);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        SharedPreferences preferences = mContext.getSharedPreferences("main", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("base_url", s);
        editor.commit();
        Log.v("BASE URL", s);
    }
}
