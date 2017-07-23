package com.mypocketstore.seasons.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.mypocketstore.seasons.AddShowActivity;
import com.mypocketstore.seasons.R;
import com.mypocketstore.seasons.adaptors.ListAdapter;

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
 * Created by anshdedha7 on 17/10/15.
 */
public class FetchHandpickedShowsTask extends AsyncTask<String, Void, ArrayList<HashMap<String, String>>> {
    Context mContext;
    ListAdapter mAdapter;
    public FetchHandpickedShowsTask(Context c, ListAdapter adapter){
        mContext = c;
        mAdapter = adapter;
    }



    private ArrayList<HashMap<String, String>> parseJson(String rawStr) throws JSONException {

        try{
            ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
            JSONObject parentObject = new JSONObject(rawStr);
            String RAW = parentObject.getString("description");
            String[] show = RAW.split("BbBBbB");

            for(int i = 0 ; i < show.length ; i++){
                String[] value = show[i].split("AaAAaA");
                HashMap<String, String> map = new HashMap<>();
                map.put("poster", value[0]);
                map.put("title", value[1]);
                map.put("avg_vote", value[2]);
                map.put("id", value[3]);
                map.put("voteCount", value[4]);
                map.put("genres", value[5]);
                arrayList.add(map);
            }

            /*Log.v("COUNT", "" + valueCount);

            for(int i = 0; i < valueCount/14; i++) {

                Log.v("I KI VALUE", "" + i);


                int run_time = Integer.parseInt(value[14 * i]);
                String first_air_date = value[(14 * i) + 1];
                String genresString = value[(14 * i) + 2] + " " + value[(14 * i) + 3];
                int show_Id = Integer.parseInt(value[(14 * i) + 4]);
                int in_prod;
                if (value[(14 * i) + 5].equals("true"))
                    in_prod = 1;
                else
                    in_prod = 0;

                String title = value[(14 * i) + 6];
                returnStr[i] = title;
                int episode_count = Integer.parseInt(value[(14 * i) + 7]);
                int season_count = Integer.parseInt(value[(14 * i) + 8]);
                String overview = value[(14 * i) + 9];
                double popularity = Double.parseDouble(value[(14 * i) + 10]);
                String poster_path = value[(14 * i) + 11];
                double vote_avg = Double.parseDouble(value[(14 * i) + 12]);
                int vote_count = Integer.parseInt(value[(14 * i) + 13]);


                Log.v("YAYAYAYA", show_Id + title + poster_path + popularity + genresString + first_air_date + run_time
                        + vote_avg + vote_count + overview + episode_count + season_count + in_prod);
            }*/

            return arrayList;

        }catch (JSONException e) {
            Log.e("ABRA KA DABRA", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
        final String LOG_TAG = "LOG TAG";
        String string_url = params[0];
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
                Toast t = new Toast(mContext).makeText(mContext, "No Internet Connection", Toast.LENGTH_SHORT);
                t.show();
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
        //mAdapter.clear();
        super.onPostExecute(list);
        if (list != null){
            mAdapter.addAll(list);
        }
    }
}

