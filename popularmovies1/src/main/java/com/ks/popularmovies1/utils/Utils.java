package com.ks.popularmovies1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ks.popularmovies1.data.MovieContract;

/**
 * Created by karn.shah on 26-12-2015.
 */
public class Utils {

    public static final String MOVIE_DETAIL_KEY = "MOVIE_DETAIL"; // key used to save state of DetailFragment and pass data from MoviesFragment to DetailActivity and DetailFragment.
    public static final String IMG_END_POINT = "http://image.tmdb.org/t/p/"; // end point used to load movie poster and backdrop images.
    public static final String END_POINT_PATH = "http://api.themoviedb.org/3"; // end point used by API http request.
    public static final String API_KEY = "";
    public static final String YOUTUBE_VIDEO_END_POINT = "https://www.youtube.com/watch?v="; // endpoint used to load a youtube video using youtube page or android app
    public static final String YOUTUBE_IMAGE_END_POINT = "http://img.youtube.com/vi/"; // endpoint used to load youtube trailer thumbs images.

    public static final String[] MOVIE_DETAIL_COLUMNS = new String[] {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_FAVORITE,
            MovieContract.MovieEntry.COLUMN_WATCHED
    };


    public static class MovieColumnIndex {
        public static final int COL_MOVIE_ID = 0;
        public static final int COL_ORIGINAL_TITLE = 1;
        public static final int COL_RELEASE_DATE = 2;
        public static final int COL_POPULARITY = 3;
        public static final int COL_VOTE_AVERAGE = 4;
        public static final int COL_OVERVIEW = 5;
        public static final int COL_BACKDROP_IMAGE_PATH = 6;
        public static final int COL_POSTER_IMAGE_PATH = 7;
        public static final int COL_FAVORITE = 8;
        public static final int COL_WATCHED = 9;
    }

    public static final String[] REVIEW_COLUMNS = new String [] {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT
    };

    public static class ReviewColumnIndex {
        public static final int _ID = 0;
        public static final int COL_REVIEW_ID = 1;
        public static final int COL_MOVIE_ID = 2;
        public static final int COL_AUTHOR = 3;
        public static final int COL_CONTENT = 4;
    }

    public static final String[] TRAILER_COLUMNS = new String [] {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_SOURCE,
            MovieContract.TrailerEntry.COLUMN_NAME
    };

    public static class TrailerColumnIndex {
        public static final int _ID = 0;
        public static final int COL_MOVIE_ID = 1;
        public static final int COL_SOURCE = 2;
        public static final int COL_NAME = 3;
    }

    public static final String[] MOVIES_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE
    };

    public static class MovieColumnsIndex {
        public static final int COL_MOVIE_ID = 0;
        public static final int COL_MOVIE_POSTER = 1;
        public static final int COL_MOVIE_TITLE = 2;
    }

    /**
     * Method to check if the device have access to internet
     * @param context
     * @return true if internet connection is available
     */
    public static boolean isNetworkConnected(Context context) {

        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (cm != null) {
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

                return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            }
        }

        return false;
    }
}
