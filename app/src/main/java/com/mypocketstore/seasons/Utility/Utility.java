package com.mypocketstore.seasons.Utility;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mypocketstore.seasons.DetailActivity;
import com.mypocketstore.seasons.data.DatabaseContract;

import java.io.File;

/**
 * Created by anshdedha7 on 07/11/15.
 */
public class Utility {

    public static final String DEVELOPER_KEY = "AIzaSyAXwh_GeJ7hp5b6S3qyvA6wEQJg0Fvdh4o";
    public static final String API_KEY = "?api_key=7210257ce98ebcdcc9e4daa7aa235114";

    public static void hide_keyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if(view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void removeShowFromDb(String showId, Context context) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(
                    DatabaseContract.ShowEntry.CONTENT_URI,
                    new String[]{DatabaseContract.ShowEntry._ID,
                            DatabaseContract.ShowEntry.COLUMN_POSTER_PATH
                    },
                    DatabaseContract.ShowEntry.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(showId)},
                    null
            );

            if (cursor != null) {
                cursor.moveToFirst();
            }

            int _IDshow = 0;
            String poster = null;

            if (cursor != null) {
                _IDshow = cursor.getInt(0);
                poster = cursor.getString(1);
            }

            ContextWrapper cw = new ContextWrapper(context);
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File image = null;
            if (poster != null) {
                image = new File(directory,poster);
            }
            if (image != null) {
                image.delete();
            }

            cursor = contentResolver.query(
                    DatabaseContract.SeasonEntry.CONTENT_URI,
                    new String[]{DatabaseContract.SeasonEntry._ID
                    },
                    DatabaseContract.SeasonEntry.COLUMN_SHOW_KEY + " = ?",
                    new String[]{String.valueOf(_IDshow)},
                    null
            );

            if (cursor != null) {
                cursor.moveToFirst();
            }

            if (cursor != null) {
                for (int i = 0; i < cursor.getCount(); i++) {
                    int _IDseason = cursor.getInt(0);
                    contentResolver.delete(DatabaseContract.EpisodeEntry.CONTENT_URI,
                            DatabaseContract.EpisodeEntry.COLUMN_SEASON_KEY + " = ?",
                            new String[]{String.valueOf(_IDseason)});
                    cursor.moveToNext();
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            contentResolver.delete(DatabaseContract.SeasonEntry.CONTENT_URI,
                    DatabaseContract.SeasonEntry.COLUMN_SHOW_KEY + " = ?",
                    new String[]{String.valueOf(_IDshow)}
            );

            contentResolver.delete(DatabaseContract.ShowEntry.CONTENT_URI,
                    DatabaseContract.ShowEntry._ID + " = ?",
                    new String[]{String.valueOf(_IDshow)}
            );
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static boolean existsInDatabase(String id, Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                DatabaseContract.ShowEntry.CONTENT_URI,
                null,
                DatabaseContract.ShowEntry.COLUMN_ID + " = ?",
                new String[]{id},
                null
        );
        boolean b = false;
        if (cursor!=null) {
            b = cursor.moveToFirst();
            cursor.close();
        }
        return b;
    }
}
