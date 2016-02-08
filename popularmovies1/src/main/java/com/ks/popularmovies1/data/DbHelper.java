package com.ks.popularmovies1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.ks.popularmovies1.data.MovieContract.*;

/**
 * Created by karn.shah on 23-01-2016.
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "popularmovies.db";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +

                MovieEntry._ID + " INTEGER PRIMARY KEY, " +

                MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
                MovieEntry.COLUMN_VIDEO + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_BACKDROP_IMAGE_PATH + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER_IMAGE_PATH + " TEXT NOT NULL, " +

                MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_WATCHED + " INTEGER NOT NULL );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +

                ReviewEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ") ON DELETE CASCADE , " +
                ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                ReviewEntry.COLUMN_CONTENT + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_REVIEW_TABLE);

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +

                TrailerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                TrailerEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL REFERENCES " + MovieEntry.TABLE_NAME + "(" + MovieEntry._ID + ") ON DELETE CASCADE , " +
                TrailerEntry.COLUMN_INDEX + " INTEGER NOT NULL, " +
                TrailerEntry.COLUMN_SOURCE + " TEXT NOT NULL, " +
                TrailerEntry.COLUMN_NAME + " TEXT NOT NULL );";

        db.execSQL(SQL_CREATE_TRAILER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(db);
    }
}
