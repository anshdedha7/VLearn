package com.mypocketstore.seasons.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseContract {

    public static final String CONTENT_AUTHORITY = "com.mypocketstore.seasons";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_SHOWS = "shows";
    public static final String PATH_SEASONS = "seasons";
    public static final String PATH_EPISODES = "episodes";

    public static final class ShowEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHOWS).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOWS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHOWS;

        public static final String TABLE_NAME = "shows";
        public static final String COLUMN_ID = "show_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_IN_PROD = "in_prod";
        public static final String COLUMN_FIRST_AIR_DATE = "first_air_date";
        public static final String COLUMN_RUN_TIME = "run_time";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_VOTE_COUNT = "vote_count";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_FAVOURITE = "favourite";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_SEASON_COUNT = "season_count";
        public static final String COLUMN_EPISODE_COUNT = "episode_count";

        public static Uri buildShowUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class SeasonEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SEASONS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEASONS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SEASONS;

        public static final String TABLE_NAME = "seasons";
        public static final String COLUMN_SHOW_KEY = "show_key";
        public static final String COLUMN_ID = "season_id";
        public static final String COLUMN_EPISODE_COUNT = "episode_count";
        public static final String COLUMN_AIR_DATE = "air_date";
        public static final String COLUMN_SEASON_NUMBER = "season_number";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";

        public static Uri buildSeasonUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildSeasonsWithShowId(int showId) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(showId)).build();
        }

        public static String getShowIdFromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getSeasonNumberFromUri(Uri uri){
            return uri.getPathSegments().get(2);
        }

        public static String getEpisodeNumberFromUri(Uri uri) {
            return uri.getPathSegments().get(3);
        }
    }

    public static final class EpisodeEntry implements BaseColumns{

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_EPISODES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EPISODES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EPISODES;

        public static final String TABLE_NAME = "episodes";
        public static final String COLUMN_SEASON_KEY = "season_key";
        public static final String COLUMN_EPISODE_ID = "episode_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BACKDROP = "backdrop";
        public static final String COLUMN_DATE = "air_date";
        public static final String COLUMN_SET_WATCHED = "set_watched";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_EPISODE_NO = "episode_no";

        public static Uri buildEpisodesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildEpisodesInSeason(int ShowId, int SeasonNumber) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(ShowId))
                    .appendPath(Integer.toString(SeasonNumber))
                    .build();
        }

        public static Uri buildSingleEpisode(int ShowId, int SeasonNumber, int EpisodeNumber) {
            return CONTENT_URI.buildUpon().appendPath(Integer.toString(ShowId))
                    .appendPath(Integer.toString(SeasonNumber))
                    .appendPath(Integer.toString(EpisodeNumber))
                    .build();
        }
    }
}
