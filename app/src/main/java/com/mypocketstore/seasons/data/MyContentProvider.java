package com.mypocketstore.seasons.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by anshdedha7 on 03/10/15.
 */
public class MyContentProvider extends ContentProvider{

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DatabaseHelper mOpenHelper;

    static final int SEASONS = 200;
    static final int SHOWS = 100;
    static final int EPISODES = 300;
    static final int SEASONS_IN_SHOW = 201;
    static final int EPISODES_IN_SEASON = 301;
    static final int SINGLE_EPISODE = 302;

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DatabaseContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, DatabaseContract.PATH_SHOWS, SHOWS);
        matcher.addURI(authority, DatabaseContract.PATH_SEASONS, SEASONS);
        matcher.addURI(authority, DatabaseContract.PATH_EPISODES, EPISODES);
        matcher.addURI(authority, DatabaseContract.PATH_SEASONS + "/#" , SEASONS_IN_SHOW);
        matcher.addURI(authority, DatabaseContract.PATH_EPISODES + "/#/#", EPISODES_IN_SEASON);
        matcher.addURI(authority, DatabaseContract.PATH_EPISODES + "/#/#/#", SINGLE_EPISODE);

        return matcher;

    }

    public static final String sShowIdSelection = DatabaseContract.ShowEntry.TABLE_NAME +
            "." + DatabaseContract.ShowEntry.COLUMN_ID + " = ? ";
    public static final String sShowIdAndSeasonNumberSelection = DatabaseContract.ShowEntry.TABLE_NAME +
            "." + DatabaseContract.ShowEntry.COLUMN_ID + " = ? AND " +
            DatabaseContract.SeasonEntry.TABLE_NAME +
            "." + DatabaseContract.SeasonEntry.COLUMN_SEASON_NUMBER + " = ?";
    public static final String sEpisodeSelection = DatabaseContract.ShowEntry.TABLE_NAME +
            "." + DatabaseContract.ShowEntry.COLUMN_ID + " = ? AND " +
            DatabaseContract.SeasonEntry.TABLE_NAME +
            "." + DatabaseContract.SeasonEntry.COLUMN_SEASON_NUMBER + " = ? AND " +
            DatabaseContract.EpisodeEntry.TABLE_NAME +
            "." + DatabaseContract.EpisodeEntry.COLUMN_EPISODE_NO + " = ?"
            ;

    private static final SQLiteQueryBuilder sQueryBuilder;

    static {
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(
                DatabaseContract.SeasonEntry.TABLE_NAME + " INNER JOIN " +
                        DatabaseContract.ShowEntry.TABLE_NAME +
                        " ON " + DatabaseContract.SeasonEntry.TABLE_NAME +
                        "." + DatabaseContract.SeasonEntry.COLUMN_SHOW_KEY +
                        " = " + DatabaseContract.ShowEntry.TABLE_NAME +
                        "." + DatabaseContract.ShowEntry._ID + " INNER JOIN " +
                        DatabaseContract.EpisodeEntry.TABLE_NAME +
                        " ON " + DatabaseContract.SeasonEntry.TABLE_NAME +
                        "." + DatabaseContract.SeasonEntry._ID +
                        " = " + DatabaseContract.EpisodeEntry.TABLE_NAME +
                        "." + DatabaseContract.EpisodeEntry.COLUMN_SEASON_KEY

        );
    }

    private Cursor getEpisodesInSeason(Uri uri, String[] projection, String sortOrder){
        String showId = DatabaseContract.SeasonEntry.getShowIdFromUri(uri);
        String seasonNumber = DatabaseContract.SeasonEntry.getSeasonNumberFromUri(uri);

        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sShowIdAndSeasonNumberSelection,
                new String[]{showId, seasonNumber},
                null,
                null,
                sortOrder);
    }

    private Cursor getSingleEpisode(Uri uri, String[] projection, String sortOrder){
        String showId = DatabaseContract.SeasonEntry.getShowIdFromUri(uri);
        String seasonNumber = DatabaseContract.SeasonEntry.getSeasonNumberFromUri(uri);
        String episodeNumber = DatabaseContract.SeasonEntry.getEpisodeNumberFromUri(uri);

        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sEpisodeSelection,
                new String[]{showId, seasonNumber, episodeNumber},
                null,
                null,
                sortOrder);
    }

    private Cursor getSeasonsInShow(Uri uri, String[] projection, String sortOrder){
        String showId = DatabaseContract.SeasonEntry.getShowIdFromUri(uri);
        String[] selectionArgs;
        String selection;

        selection = sShowIdSelection;
        selectionArgs = new String[]{showId};

        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }



        @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case(SINGLE_EPISODE):
                return DatabaseContract.EpisodeEntry.CONTENT_ITEM_TYPE;
            case(EPISODES_IN_SEASON):
                return DatabaseContract.EpisodeEntry.CONTENT_TYPE;
            case(EPISODES):
                return DatabaseContract.EpisodeEntry.CONTENT_TYPE;
            case(SHOWS):
                return DatabaseContract.ShowEntry.CONTENT_TYPE;
            case(SEASONS):
                return DatabaseContract.SeasonEntry.CONTENT_TYPE;
            case(SEASONS_IN_SHOW):
                return DatabaseContract.SeasonEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case(SEASONS_IN_SHOW):{
                retCursor = getSeasonsInShow(uri, projection, sortOrder);
                break;
            }

            case(EPISODES_IN_SEASON):{
                retCursor = getEpisodesInSeason(uri, projection, sortOrder);
                break;
            }

            case(SINGLE_EPISODE):{
                retCursor = getSingleEpisode(uri, projection, sortOrder);
                break;
            }

            case(SHOWS):{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.ShowEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case(SEASONS):{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.SeasonEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case(EPISODES):{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        DatabaseContract.EpisodeEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case(SHOWS):{
                long _id = db.insert(DatabaseContract.ShowEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = DatabaseContract.ShowEntry.buildShowUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case(SEASONS):{
                long _id = db.insert(DatabaseContract.SeasonEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = DatabaseContract.SeasonEntry.buildSeasonUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case(EPISODES):{
                long _id = db.insert(DatabaseContract.EpisodeEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = DatabaseContract.EpisodeEntry.buildEpisodesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";

        switch (match){
            case(SHOWS):{
                rowsDeleted = db.delete(
                        DatabaseContract.ShowEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case (SEASONS):{
                rowsDeleted = db.delete(
                        DatabaseContract.SeasonEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case (EPISODES):{
                rowsDeleted = db.delete(
                        DatabaseContract.EpisodeEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case (SHOWS): {
                rowsUpdated = db.update(DatabaseContract.ShowEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case (SEASONS): {
                rowsUpdated = db.update(DatabaseContract.SeasonEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            case (EPISODES): {
                rowsUpdated = db.update(DatabaseContract.EpisodeEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case (SEASONS): {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DatabaseContract.SeasonEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            case (EPISODES): {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(DatabaseContract.EpisodeEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
