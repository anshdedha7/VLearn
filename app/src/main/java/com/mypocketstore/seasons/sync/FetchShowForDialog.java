package com.mypocketstore.seasons.sync;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mypocketstore.seasons.AddShowDialogFragment;
import com.mypocketstore.seasons.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by anshdedha7 on 18/10/15.
 */
public class FetchShowForDialog extends AsyncTask<Integer, Void, String[]> {

    View mView;
    Context mContext;

    public FetchShowForDialog(View view, Context c){
        mView = view;
        mContext = c;
    }

    private String[] getShowFromJson(String rawStr) throws JSONException {
        try {
            JSONObject parentObject = new JSONObject(rawStr);
            int show_Id = Integer.parseInt(parentObject.getString("id"));
            String title = parentObject.getString("name");
            String poster_path = parentObject.getString("poster_path");
            String genresString = null;
            JSONArray genres = parentObject.getJSONArray("genres");
            for (int i = 0; i < genres.length(); i++) {
                if (i > 0) {
                    genresString = genresString + ", " + genres.getJSONObject(i).getString("name");
                } else {
                    genresString = genres.getJSONObject(i).getString("name");
                }
            }

            Log.v("%%%%", "1");
            String first_air_date = parentObject.getString("first_air_date");
            String run_time = null;
            JSONArray run_time_array = parentObject.getJSONArray("episode_run_time");
            for (int i = 0; i < run_time_array.length(); i++) {
                if (i > 0) {
                    run_time = run_time + ", " + run_time_array.getInt(i);
                } else {
                    run_time = Integer.toString(run_time_array.getInt(i));
                }
            }
            double vote_avg = parentObject.getDouble("vote_average");
            int vote_count = parentObject.getInt("vote_count");
            String overview = parentObject.getString("overview");
            int episode_count = parentObject.getInt("number_of_episodes");
            int season_count = parentObject.getInt("number_of_seasons");
            boolean in_prod_boolean = parentObject.getBoolean("in_production");
            String inProd;
            if (!in_prod_boolean){
                inProd = "Ended";
            } else {
                inProd = "Running";
            }
            double popularity_double = parentObject.getDouble("popularity");
            int popularity = (int) popularity_double;

            String[] returnArray = {poster_path,
                    title,
                    String.valueOf(vote_avg),
                    String.valueOf(vote_count),
                    overview,
                    String.valueOf(episode_count),
                    String.valueOf(season_count),
                    inProd,
                    first_air_date,
                    String.valueOf(popularity),
                    genresString};
            Log.v("%%%%", "2");
            return returnArray;
        }catch (JSONException e){
            Log.e("ABRA KA DABRA", e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected String[] doInBackground(Integer... params) {

        final String api_key = "?api_key=7210257ce98ebcdcc9e4daa7aa235114";
        final String base_url = "http://api.themoviedb.org/3";
        String builtUrl = base_url + "/tv/" + params[0] + api_key;
        Log.v("SHOW URL", builtUrl);

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

            return getShowFromJson(showsJsonString);
        }catch (IOException e) {
            Log.e("LOG TAG", "Error ", e);
            return null;
        } catch (JSONException e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("LOG TAG", "Error closing stream", e);
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        super.onPostExecute(strings);

        (mView.findViewById(R.id.progressBarAdd)).setVisibility(View.GONE);

        ImageView iv = ((ImageView) mView.findViewById(R.id.imageViewAddPoster));
        if(!strings[0].equals("null")) {
            FetchThumbnailFromServer f = new FetchThumbnailFromServer(mContext, iv);
            f.execute(strings[0]);
        }

        TextView t1 = (TextView) mView.findViewById(R.id.textViewAddTitle);
        if (t1 != null){
            t1.setText(strings[1]);
        }

        t1 = (TextView) mView.findViewById(R.id.textViewAddRatingValue);
        if (t1 != null){
            t1.setText(strings[2]);
        }

        (mView.findViewById(R.id.imageViewStar)).setVisibility(View.VISIBLE);

        t1 = (TextView) mView.findViewById(R.id.textViewAddRatingRange);
        if (t1 != null){
            String s = "(" + strings[3] + ") votes";
            t1.setText(s);
        }

        t1 = (TextView) mView.findViewById(R.id.textViewAddDescription);
        if (t1 != null){
            t1.setText(strings[4]);
        }

        t1 = (TextView) mView.findViewById(R.id.textViewAddGenres);
        if (t1 != null){
            t1.setText(strings[10]);
        }

        t1 = (TextView) mView.findViewById(R.id.textViewAddReleased);
        if (t1 != null){
            t1.setText(strings[8]);
        }



        String meta = strings[7] + "\nSeasons: " + strings[5] + "\nEpisodes: " + strings[6]
               // + "\nPopularity: " + strings[9]
                ;
        t1 = (TextView) mView.findViewById(R.id.textViewAddShowMeta);
        if (t1 != null){
            t1.setText(meta);
        }


    }
}
