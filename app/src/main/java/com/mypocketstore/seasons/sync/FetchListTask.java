package com.mypocketstore.seasons.sync;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

import com.mypocketstore.seasons.data.DatabaseContract.ShowEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class FetchListTask extends AsyncTask<String, Void, String>{


    private final String LOG_TAG = "LOG TAG";

    private int mFlag = 0;

    private final Context mContext;

    public FetchListTask(Context context) {
        mContext = context;
    }

    long addShowToDatabase(int show_Id, String title, String poster_path, String genresString, String first_air_date, String run_time,
                           double vote_avg, int vote_count, String overview, int episode_count,
                           int season_count, int in_prod, double popularity){
        long showId;

        Cursor showCursor = mContext.getContentResolver().query(
                ShowEntry.CONTENT_URI,
                new String[]{ShowEntry._ID},
                ShowEntry.COLUMN_ID + " = ?",
                new String[]{String.valueOf(show_Id)},
                null
        );

        if (showCursor.moveToFirst()){
            int showIdIndex = showCursor.getColumnIndex(ShowEntry._ID);
            showId = showCursor.getInt(showIdIndex);
            mFlag = 1;
        }else{
            ContentValues listValues = new ContentValues();

            listValues.put(ShowEntry.COLUMN_ID, show_Id);
            listValues.put(ShowEntry.COLUMN_TITLE, title);
            listValues.put(ShowEntry.COLUMN_POSTER_PATH, poster_path);
            listValues.put(ShowEntry.COLUMN_GENRES, genresString);
            listValues.put(ShowEntry.COLUMN_FIRST_AIR_DATE, first_air_date);
            listValues.put(ShowEntry.COLUMN_RUN_TIME, run_time);
            listValues.put(ShowEntry.COLUMN_VOTE_AVERAGE, vote_avg);
            listValues.put(ShowEntry.COLUMN_VOTE_COUNT, vote_count);
            listValues.put(ShowEntry.COLUMN_OVERVIEW, overview);
            listValues.put(ShowEntry.COLUMN_EPISODE_COUNT, episode_count);
            listValues.put(ShowEntry.COLUMN_SEASON_COUNT, season_count);
            listValues.put(ShowEntry.COLUMN_IN_PROD, in_prod);
            listValues.put(ShowEntry.COLUMN_POPULARITY, popularity);

            Uri insertedUri = mContext.getContentResolver().insert(ShowEntry.CONTENT_URI, listValues);
            showId = ContentUris.parseId(insertedUri);
            //mFlag = 0;
        }

            showCursor.close();
            return showId;
    }
    private String getShowFromJson(String rawStr) throws JSONException {
        try{
            JSONObject parentObject = new JSONObject(rawStr);
            int show_Id = Integer.parseInt(parentObject.getString("id"));
            String title = parentObject.getString("name");
            String poster_path = parentObject.getString("poster_path");
            String genresString = null;
            JSONArray genres = parentObject.getJSONArray("genres");
            for (int i = 0; i < genres.length(); i++){
                if(i > 0) {
                    genresString = genresString + ", " + genres.getJSONObject(i).getString("name");
                }else {
                    genresString = genres.getJSONObject(i).getString("name");
                }
            }
            String first_air_date = parentObject.getString("first_air_date");
            String run_time = null;
            JSONArray run_time_array = parentObject.getJSONArray("episode_run_time");
            for (int i = 0; i < run_time_array.length(); i++){
                if(i > 0) {
                    run_time = run_time + ", " + run_time_array.getInt(i);
                }else{
                    run_time = Integer.toString(run_time_array.getInt(i));
                }
            }
            double vote_avg = parentObject.getDouble("vote_average");
            int vote_count = parentObject.getInt("vote_count");
            String overview = parentObject.getString("overview");
            int episode_count = parentObject.getInt("number_of_episodes");
            int season_count = parentObject.getInt("number_of_seasons");
            boolean in_prod_boolean = parentObject.getBoolean("in_production");
            int in_prod;
            if (in_prod_boolean){
                in_prod = 1;
            }else{
                in_prod = 0;
            }
            double popularity = parentObject.getDouble("popularity");

            long showId = addShowToDatabase(show_Id, title, poster_path, genresString, first_air_date,
                   run_time, vote_avg, vote_count, overview, episode_count, season_count, in_prod, popularity);

            String returnString = String.valueOf(showId)+ " " + show_Id + " " + String.valueOf(season_count);
            return returnString;
        }catch (JSONException e){
            Log.e("ABRA KA DABRA", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    /*private String[] getSearchResultFromJSON(String rawStr) throws JSONException {

        try{
            JSONObject parentObj = new JSONObject(rawStr);
            JSONArray resultArray = parentObj.getJSONArray("results");
            String[] returnStr = new String[resultArray.length()];
            for (int i = 0; i < resultArray.length(); i++){
                JSONObject show = resultArray.getJSONObject(i);
                returnStr[i] = show.getString("name");
            }
            return returnStr;

        }catch (JSONException e) {
            Log.e("ABRA KA DABRA", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }*/

    /*private String[] getShowDataFromJson(String rawStr) throws JSONException {


        try{
            JSONObject parentObject = new JSONObject(rawStr);
            String RAW = parentObject.getString("description");
            String[] value = RAW.split("AaAAaA");
            for(String temp : value){
                Log.v("VALUES", temp);
            }

            int showCount = value.length;
            String[] returnStr = new String[showCount/14];
            Log.v("COUNT", "" + showCount);

            for(int i = 0; i < showCount/14; i++) {

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
            }

            return returnStr;

        }catch (JSONException e) {
            Log.e("ABRA KA DABRA", e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }*/

    @Override
    protected String doInBackground(String... params) {

        final String api_key = "?api_key=7210257ce98ebcdcc9e4daa7aa235114";
        final String base_url = "http://api.themoviedb.org/3";


        String showId = params[0];
        String builtUrl = base_url + "/tv/" + showId + api_key;

        /*if (params.length == 1) {
            String query = params[0];
            try {
                builtUrl = base_url + "/search/tv" + api_key + "&query=" + URLEncoder.encode(query, "UTF-8");
            }catch (UnsupportedEncodingException e){
                Log.e(LOG_TAG, "Unsupported Encoding Exception");
            }
            Log.v("SEARCH URL",builtUrl);

        }*/

        /*if (params.length == 0) {
            final String list_tag = "/list";
            final String list_id = "/560eb14e9251413f0900341d";
            builtUrl = base_url + list_tag + list_id + api_key;
            Log.d("MY TAG", builtUrl);
        }*/

        String showsJsonString;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(builtUrl);
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

            showsJsonString = buffer.toString();
            Log.d("MY TAG", showsJsonString);

        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
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

        /*if(params.length == 0){
            try{
                return getShowDataFromJson(showsJsonString);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }*/
        /*}else if (params.length == 1){
            try {
                return getSearchResultFromJSON(showsJsonString);
            }catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }*/
        /*}else */
        try {

            return getShowFromJson(showsJsonString);
        }catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            }

        return null;
    }

    protected void onPostExecute(String result) {
        if (mFlag==0) {
            AddSeasonsToDbTask addSeasonsToDbTask = new AddSeasonsToDbTask(mContext);
            addSeasonsToDbTask.execute(result);
        }else{
            Toast.makeText(mContext, "Show already exists!", Toast.LENGTH_SHORT).show();
        }

    }
}