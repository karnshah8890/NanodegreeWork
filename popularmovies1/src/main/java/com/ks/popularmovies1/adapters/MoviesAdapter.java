package com.ks.popularmovies1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.models.Movie;
import com.ks.popularmovies1.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by karn.shah on 26-12-2015.
 */
public class MoviesAdapter extends ArrayAdapter<Movie> {

    // default image size, all image result must have the same size
    private static int DEFAULT_IMG_WIDTH = 185;
    private static int DEFAULT_IMG_HEIGHT = 278;

    public MoviesAdapter(Context context, int layout, List<Movie> movies) {
        super(context, layout, movies);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.row_grid_movies, parent, false);
        }

        // we set the title to TextView
        final TextView title = (TextView) convertView.findViewById(R.id.tv_movie_title);
        title.setVisibility(View.GONE);
        title.setText(movie.getOriginalTitle());

        ImageView poster = (ImageView) convertView.findViewById(R.id.iv_movie_poster);

        if (movie.getPosterPath() != null) {

            // create an instance of Picasso using the context
            Picasso p = Picasso.with(getContext());

            // debugging purpose
            //p.setLoggingEnabled(true);

            // load the poster image
            p.load(Utils.IMG_END_POINT + "w185" + movie.getPosterPath())
                    // if the image don't exist we use a default drawable
                    .error(R.drawable.poster_missing)
                    .resize(DEFAULT_IMG_WIDTH, DEFAULT_IMG_HEIGHT)

                            // put the result image in poster ImageView
                    .into(poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            title.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError() {
                            title.setVisibility(View.VISIBLE);
                        }
                    });

        } else {
            // Default without poster image
            poster.setImageResource(R.drawable.poster_missing);
            title.setVisibility(View.VISIBLE);
        }

        return convertView;
    }
}
