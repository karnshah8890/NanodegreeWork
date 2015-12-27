package com.ks.popularmovies1.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.models.Movie;
import com.ks.popularmovies1.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * Created by karn.shah on 27-12-2015.
 */
public class MovieDetailFragment extends Fragment {

   private Movie movie;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Utils.MOVIE_DETAIL_KEY)) {
            movie = (Movie) intent.getParcelableExtra(Utils.MOVIE_DETAIL_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.MOVIE_DETAIL_KEY, movie);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_movie_detail, container, false);
        if (savedInstanceState != null && movie == null && savedInstanceState.containsKey(Utils.MOVIE_DETAIL_KEY)) {
            movie = (Movie) savedInstanceState.getParcelable(Utils.MOVIE_DETAIL_KEY);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (movie == null) {
            // TODO: Show error getting movie data ...

            return;
        }

        TextView tv = (TextView) view.findViewById(R.id.tvOriginalTitle);
        tv.setText(movie.getOriginalTitle());

        tv = (TextView) view.findViewById(R.id.tvOverview);
        tv.setText(movie.getOverview());

        tv = (TextView) view.findViewById(R.id.tvDateRelease);
        tv.setText(getString(R.string.movie_detail_release_date) + ": " + movie.getReleaseDate());

        tv = (TextView) view.findViewById(R.id.tvVoteAverage);
        tv.setText(getString(R.string.movie_detail_rating) + ": " + movie.getVoteAverage());

        final ImageView ivPoster = (ImageView) view.findViewById(R.id.ivDetailPoster);

        // create an instance of Picasso using the context
        Picasso p = Picasso.with(getActivity());

        // debugging purpose
        // p.setLoggingEnabled(true);

        // load the backdrop image
        p.load(Utils.IMG_END_POINT + "w185" + movie.getPosterPath())
                // if the image don't exist we use a default drawable
                .error(R.drawable.poster_missing)
                        // put the result image in poster ImageView
                .into(ivPoster);
    }
}
