package com.ks.popularmovies1.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.activities.MovieDetailActivity;
import com.ks.popularmovies1.adapters.MoviesAdapter;
import com.ks.popularmovies1.models.DiscoverResult;
import com.ks.popularmovies1.models.Movie;
import com.ks.popularmovies1.utils.Utils;
import com.ks.popularmovies1.webservices.WebService;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by karn.shah on 26-12-2015.
 */
public class MovieListFragment extends Fragment {

    private static final String GRID_SCROLL_KEY = "GRID_INDEX";

    private String sortValue = null;
    private int gridScrollValue = 0;

    private MoviesAdapter mAdapter;
    private GridView mGVMovies;

    private ProgressDialog mPDLoading;
    private LinearLayout mLLOffline;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        if (sortValue == null) {
            // get the sort value from shared preferences
            sortValue =getArguments().getString(getString(R.string.pref_sort_key));
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(GRID_SCROLL_KEY))
            gridScrollValue = savedInstanceState.getInt(GRID_SCROLL_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCompo();
        if (Utils.isNetworkConnected(getActivity())) {
            loadMovies();
        }else{
            if (mLLOffline != null)
                mLLOffline.setVisibility(View.VISIBLE);

            if (mGVMovies != null)
                mGVMovies.setVisibility(View.GONE);

            // hide the loading ProgressDialog
            if (mPDLoading!=null && mPDLoading.isShowing())
                mPDLoading.dismiss();
        }

    }

    private void loadMovies() {
        if (mPDLoading == null)
            mPDLoading = new ProgressDialog(getActivity(), ProgressDialog.STYLE_SPINNER);

        mPDLoading.setIndeterminate(true);
        mPDLoading.setMessage(getString(R.string.loading_message));
        mPDLoading.show();
        WebService.setAdapter().discover(sortValue, new Callback<DiscoverResult>() {
            @Override
            public void success(DiscoverResult discoverResult, Response response) {
                if (mGVMovies != null && mGVMovies.getVisibility() == View.GONE)
                    mGVMovies.setVisibility(View.VISIBLE);

                if (mLLOffline != null && mLLOffline.getVisibility() == View.VISIBLE)
                    mLLOffline.setVisibility(View.GONE);

                // cleaning the MovieAdapter
                mAdapter.clear();

                if (discoverResult != null && discoverResult.results != null && !discoverResult.results.isEmpty()) {
                    // we use the addAll method to avoid the consecutive notifyDataSetChanged invoke
                    mAdapter.addAll(discoverResult.results);

                    // scrolling to last scroll saved status
                    mGVMovies.setSelection(gridScrollValue);
                }

                // hide the loading ProgressDialog
                if (mPDLoading!=null && mPDLoading.isShowing())
                    mPDLoading.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                // show an error API request
                if (mPDLoading!=null && mPDLoading.isShowing())
                    mPDLoading.dismiss();
//                Toast.makeText(getActivity(), "HTTP API ERROR", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCompo() {
// getting an instance of offline message...
        mLLOffline = (LinearLayout) getView().findViewById(R.id.llOffline);

        // creating a MovieAdapter using movie_poster layout for each movie result
        mAdapter = new MoviesAdapter(getActivity(), R.layout.row_grid_movies, new ArrayList<Movie>());

        mGVMovies = (GridView) getView().findViewById(R.id.gvMovies);
        // set the MoviesAdapter to GridView
        mGVMovies.setAdapter(mAdapter);

        mGVMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start detail activity using existent Movie data
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(Utils.MOVIE_DETAIL_KEY, mAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saving scroll position
        outState.putInt(GRID_SCROLL_KEY, mGVMovies.getFirstVisiblePosition());
        super.onSaveInstanceState(outState);
    }
}
