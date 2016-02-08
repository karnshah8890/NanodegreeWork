package com.ks.popularmovies1.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.data.MovieContract;
import com.ks.popularmovies1.data.MovieProvider;
import com.ks.popularmovies1.models.DiscoverResult;
import com.ks.popularmovies1.models.Movie;
import com.ks.popularmovies1.utils.Utils;
import com.ks.popularmovies1.webservices.WebService;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    private final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();

    // Interval at which to sync with TMDb API, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        mContentResolver = context.getContentResolver();
    }

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // checking if the device have internet connection
        if (Utils.isNetworkConnected(getContext())) {

            final List<Movie> movies = new ArrayList<>();

            Log.d(LOG_TAG, "Requesting to TMDb API using " + MovieProvider.FILTER_BY_POPULARITY);

            WebService.setAdapter().discover(MovieProvider.FILTER_BY_POPULARITY, new Callback<DiscoverResult<Movie>>() {
                @Override
                public void success(DiscoverResult<Movie> discover, Response response) {
                    if (discover != null && discover.results != null && !discover.results.isEmpty()){
                        movies.addAll(discover.results);
                        Log.d(LOG_TAG, "discover.results count : " + discover.results.size());
                    }

                    Log.d(LOG_TAG, "Requesting to TMDb API using " + MovieProvider.FILTER_BY_VOTEAVERAGE);
                    WebService.setAdapter().discover(MovieProvider.FILTER_BY_VOTEAVERAGE, new Callback<DiscoverResult<Movie>>() {
                        @Override
                        public void success(DiscoverResult<Movie> discover, Response resp) {
                            for (Movie m : discover.results) {
                                if (!movies.contains(m))
                                    movies.add(m);
                            }

                            syncMovies(movies);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.d(LOG_TAG, "Error during Request to TMDb API: " + error.getMessage());
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    // show an error API request
                    Log.d(LOG_TAG, "Error during Request to TMDb API: " + error.getMessage());
//                Toast.makeText(getActivity(), "HTTP API ERROR", Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Log.d(LOG_TAG, "Internet is not available.");
        }

    }

    private void syncMovies(List<Movie> movies) {

        if (movies != null && !movies.isEmpty()) {

            Log.d(LOG_TAG, "Movies to sync size: " + movies.size());

            Vector<ContentValues> vMovies = new Vector<>(movies.size());
            List<String> moviesId = new ArrayList<>();

            for (Movie m : movies) {
                moviesId.add(Long.toString(m.getId()));

                ContentValues values = new ContentValues();

                values.put(MovieContract.MovieEntry._ID, m.getId());
                values.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, m.getOriginalTitle());
                values.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, m.getOverview());
                values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, m.getReleaseDate());
                values.put(MovieContract.MovieEntry.COLUMN_POPULARITY, m.getPopularity());
                values.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, m.getVoteAverage());
                values.put(MovieContract.MovieEntry.COLUMN_POSTER_IMAGE_PATH, m.getPosterPath());
                values.put(MovieContract.MovieEntry.COLUMN_BACKDROP_IMAGE_PATH, m.getBackdropPath());
                values.put(MovieContract.MovieEntry.COLUMN_VIDEO, 0);
                values.put(MovieContract.MovieEntry.COLUMN_WATCHED, 0);
                values.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);

                vMovies.add(values);
            }

            int insertOrUpdatedRowMovies = 0;

            if (vMovies.size() > 0) {
                ContentValues[] cvArray = new ContentValues[vMovies.size()];
                vMovies.toArray(cvArray);

                Log.d(LOG_TAG, "Bulk insert of results Movies...");

                // the bulkInsert method update a row if already exist
                insertOrUpdatedRowMovies = mContentResolver.bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);

            }

            if (insertOrUpdatedRowMovies > 0) {
                // Delete old movies, reviews and trailers using cascade
                int deletedRows = mContentResolver.delete(
                        MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " NOT IN ( " + TextUtils.join(",", moviesId) + " ) " +
                        " AND " + MovieContract.MovieEntry.COLUMN_FAVORITE + " = 0 ",
                        null);

                Log.d(LOG_TAG, "Deleted old movies: " + deletedRows);
            }

        }


    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

            // Add the account and account type, no password or user data
            // If successful, return the Account object, otherwise report an error.
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
