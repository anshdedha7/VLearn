package com.mypocketstore.seasons.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.mypocketstore.seasons.AddShowActivity;
import com.mypocketstore.seasons.adaptors.ListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by anshdedha7 on 05/11/15.
 */
public class FetchPopularTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {

    Context mContext;
    ListAdapter mAdapter;
    String LOG_TAG = "LOG TAG";

    public FetchPopularTask(Context c, ListAdapter a) {
        mContext = c;
        mAdapter = a;
    }

    private ArrayList<HashMap<String, String>> parseJson(String rawStr) throws JSONException {
        if (!rawStr.equals(null)) {
            try {
                ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
                JSONObject parentObject = new JSONObject(rawStr);
                JSONArray resultArray = parentObject.getJSONArray("results");

                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject show = resultArray.getJSONObject(i);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("poster", show.getString("poster_path"));
                    map.put("title", show.getString("name"));
                    map.put("avg_vote", show.getString("vote_average"));
                    map.put("id", show.getString("id"));
                    map.put("voteCount", show.getString("vote_count"));
                    map.put("genres", null);
                    arrayList.add(map);
                }
                return arrayList;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String string_url = "http://api.themoviedb.org/3/discover/tv?api_key=7210257ce98ebcdcc9e4daa7aa235114&sortby=popularity.desc&page="
                + params[0];
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
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<HashMap<String, String>> list) {
        super.onPostExecute(list);

        if (list != null){
            mAdapter.addAll(list);
        } else {
            Toast t = new Toast(mContext).makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT);
            t.show();
        }
    }
}
