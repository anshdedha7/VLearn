package com.mypocketstore.seasons.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mypocketstore.seasons.data.DatabaseContract.ShowEntry;
import com.mypocketstore.seasons.data.DatabaseContract.EpisodeEntry;
import com.mypocketstore.seasons.data.DatabaseContract.SeasonEntry;
/**
 * Created by anshdedha7 on 03/10/15.
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "seasons.db";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_SHOWS_TABLE = "CREATE TABLE " + ShowEntry.TABLE_NAME + " (" +
                ShowEntry._ID + " INTEGER PRIMARY KEY," +
                ShowEntry.COLUMN_TITLE + " TEXT NOT NULL," +
                ShowEntry.COLUMN_FAVOURITE + " INTEGER," +
                ShowEntry.COLUMN_FIRST_AIR_DATE + " TEXT," +
                ShowEntry.COLUMN_ID + " INTEGER NOT NULL," +
                ShowEntry.COLUMN_GENRES + " TEXT," +
                ShowEntry.COLUMN_IN_PROD + " INTEGER," +
                ShowEntry.COLUMN_OVERVIEW + " TEXT," +
                ShowEntry.COLUMN_POSTER_PATH + " TEXT," +
                ShowEntry.COLUMN_RUN_TIME + " TEXT," +
                ShowEntry.COLUMN_POPULARITY + " REAL," +
                ShowEntry.COLUMN_VOTE_AVERAGE + " REAL," +
                ShowEntry.COLUMN_VOTE_COUNT + " INTEGER," +
                ShowEntry.COLUMN_SEASON_COUNT + " INTEGER," +
                ShowEntry.COLUMN_EPISODE_COUNT + " INTEGER," +
                " UNIQUE (" + ShowEntry.COLUMN_ID + ") ON CONFLICT REPLACE" +
                " );";

        final String SQL_CREATE_SEASON_TABLE = "CREATE TABLE " + SeasonEntry.TABLE_NAME + " (" +
                SeasonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SeasonEntry.COLUMN_SHOW_KEY + " INTEGER NOT NULL," +
                SeasonEntry.COLUMN_ID + " INTEGER NOT NULL," +
                SeasonEntry.COLUMN_EPISODE_COUNT + " INTEGER NOT NULL," +
                SeasonEntry.COLUMN_AIR_DATE + " TEXT," +
                SeasonEntry.COLUMN_NAME + " TEXT," +
                SeasonEntry.COLUMN_OVERVIEW + " TEXT," +
                SeasonEntry.COLUMN_POSTER_PATH + " TEXT," +
                SeasonEntry.COLUMN_SEASON_NUMBER + " INTEGER NOT NULL," +
                " FOREIGN KEY (" + SeasonEntry.COLUMN_SHOW_KEY + ") REFERENCES " +
                ShowEntry.TABLE_NAME + " (" + ShowEntry._ID + "), " +
                " UNIQUE (" + SeasonEntry.COLUMN_ID + ") ON CONFLICT REPLACE" +
                " );";


        final String SQL_CREATE_EPISODE_TABLE = "CREATE TABLE " + EpisodeEntry.TABLE_NAME + " (" +
                EpisodeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EpisodeEntry.COLUMN_SEASON_KEY + " INTEGER NOT NULL," +
                EpisodeEntry.COLUMN_EPISODE_ID + " INTEGER NOT NULL," +
                EpisodeEntry.COLUMN_EPISODE_NO + " INTEGER NOT NULL," +
                EpisodeEntry.COLUMN_NAME + " TEXT," +
                EpisodeEntry.COLUMN_BACKDROP + " TEXT," +
                EpisodeEntry.COLUMN_DATE + " TEXT," +
                EpisodeEntry.COLUMN_OVERVIEW + " TEXT," +
                EpisodeEntry.COLUMN_SET_WATCHED + " INTEGER," +
                " FOREIGN KEY (" + EpisodeEntry.COLUMN_SEASON_KEY + ") REFERENCES " +
                SeasonEntry.TABLE_NAME + " (" + SeasonEntry._ID + "), " +
                " UNIQUE (" + EpisodeEntry.COLUMN_EPISODE_ID + ") ON CONFLICT REPLACE" +
                " );";

        db.execSQL(SQL_CREATE_SHOWS_TABLE);
        db.execSQL(SQL_CREATE_SEASON_TABLE);
        db.execSQL(SQL_CREATE_EPISODE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ShowEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SeasonEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + EpisodeEntry.TABLE_NAME);
        onCreate(db);
    }
}
