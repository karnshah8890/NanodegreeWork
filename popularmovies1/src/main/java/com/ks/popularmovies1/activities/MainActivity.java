package com.ks.popularmovies1.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.fragments.MovieListFragment;

public class MainActivity extends AppCompatActivity {

    private MovieListFragment movieListFragment;
    private String mLastSortBy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLastSortBy=getString(R.string.pref_sort_value_popularity);
        doLoadDataInBackground(mLastSortBy);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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
        if (id == R.id.action_popular && mLastSortBy.equalsIgnoreCase(getString(R.string.pref_sort_value_popularity))) {
            mLastSortBy = getString(R.string.pref_sort_value_popularity);
            doLoadDataInBackground(mLastSortBy);
        } else if (id == R.id.action_high_rate && mLastSortBy.equalsIgnoreCase(getString(R.string.pref_sort_value_rate))) {
            mLastSortBy = getString(R.string.pref_sort_value_rate);
            doLoadDataInBackground(mLastSortBy);
        }

        return super.onOptionsItemSelected(item);
    }

    private void doLoadDataInBackground(String mLastSortBy) {
        final Bundle args = new Bundle();

        args.putString(getString(R.string.pref_sort_key), mLastSortBy);
        movieListFragment = new MovieListFragment();
        movieListFragment.setArguments(args);

        // Replace
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_container, movieListFragment, MovieListFragment.class.getSimpleName()).commit();
    }
}
