package com.ks.popularmovies1.activities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.fragments.MovieDetailFragment;
import com.ks.popularmovies1.fragments.MovieListFragment;
import com.ks.popularmovies1.sync.PopularMoviesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MovieListFragment.Callback{

    private MovieListFragment movieListFragment;
    private String mLastSortBy;
    private boolean mTwoPane;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        if (findViewById(R.id.movie_detail_container) != null) {
            // two-pane using sw600dp layout
            mTwoPane = true;

            if (savedInstanceState == null) {
                getFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new MovieDetailFragment(), MovieDetailFragment.class.getSimpleName())
                        .commit();
            }

        } else {
            mTwoPane = false;
        }

        mLastSortBy=getString(R.string.pref_sort_value_popularity);
        doLoadDataInBackground(mLastSortBy);

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_popular && !mLastSortBy.equalsIgnoreCase(getString(R.string.pref_sort_value_popularity))) {
            mLastSortBy = getString(R.string.pref_sort_value_popularity);
            doLoadDataInBackground(mLastSortBy);
        } else if (id == R.id.action_high_rate && !mLastSortBy.equalsIgnoreCase(getString(R.string.pref_sort_value_rate))) {
            mLastSortBy = getString(R.string.pref_sort_value_rate);
            doLoadDataInBackground(mLastSortBy);
        }else if (id == R.id.action_fav && !mLastSortBy.equalsIgnoreCase(getString(R.string.pref_sort_value_fav))) {
            mLastSortBy = getString(R.string.pref_sort_value_fav);
            doLoadDataInBackground(mLastSortBy);
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLoadDataInBackground(String mLastSortBy) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(getString(R.string.pref_sort_key), mLastSortBy);
        editor.commit();
//        final Bundle args = new Bundle();
//
//        args.putString(getString(R.string.pref_sort_key), mLastSortBy);
//        movieListFragment = new MovieListFragment();
//        movieListFragment.setArguments(args);
//
//        // Replace
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.activity_main_container, movieListFragment, MovieListFragment.class.getSimpleName()).commit();


    }

    @Override
    public void onGridItemSelected(Uri detailUri, View shareView) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(MovieDetailFragment.DETAIL_URI_KEY, detailUri);

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, MovieDetailFragment.class.getSimpleName())
                    .commit();
        } else {
            Intent intent = new Intent(this, MovieDetailActivity.class).setData(detailUri);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && shareView != null) {
                // transition between two poster images
                View view = shareView.findViewById(R.id.iv_movie_poster);

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        view,
                        view.getTransitionName()
                ).toBundle();

                startActivity(intent, bundle);

            } else {
                startActivity(intent);
            }
        }
    }
}
