package com.ks.popularmovies1.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.fragments.MovieDetailFragment;

/**
 * Created by karn.shah on 27-12-2015.
 */
public class MovieDetailActivity extends AppCompatActivity{
    private static final String MOVIE_ID_KEY = "MOVIE_ID_KEY";
    private long mMovieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // To sync transition using poster image
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            postponeEnterTransition();

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI_KEY, getIntent().getData());

            mMovieId = Long.parseLong(getIntent().getData().getLastPathSegment());

            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);

            getFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, fragment)
                    .commit();
        } else {
            mMovieId = savedInstanceState.getLong(MOVIE_ID_KEY);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(MOVIE_ID_KEY, mMovieId);
    }
}
