package com.ks.popularmovies1.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.adapters.MoviesAdapter;
import com.ks.popularmovies1.data.MovieContract;
import com.ks.popularmovies1.utils.Utils;

/**
 * Created by karn.shah on 26-12-2015.
 */
public class MovieListFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener,LoaderManager.LoaderCallbacks<Cursor> {

    private static final String GRID_SCROLL_KEY = "GRID_INDEX";

//    private String sortValue = null;
//    private int gridScrollValue = 0;

    private MoviesAdapter mAdapter;
    private GridView mGVMovies;

//    private ProgressDialog mPDLoading;
//    private LinearLayout mLLOffline;

    private static final String SELECTED_KEY = "SELECTED_POSITION";

    private int mPosition = GridView.INVALID_POSITION;
    private static final int MOVIES_LOADER = 0;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * DetailMovieCallback for when an item has been selected.
         */
        void onGridItemSelected(Uri detailUri, View sharedView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



//        if (sortValue == null) {
//            // get the sort value from shared preferences
//            sortValue =getArguments().getString(getString(R.string.pref_sort_key));
//        }

//        if (savedInstanceState != null && savedInstanceState.containsKey(GRID_SCROLL_KEY))
//            gridScrollValue = savedInstanceState.getInt(GRID_SCROLL_KEY);
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
        mAdapter = new MoviesAdapter(getActivity(), null, 0);

        mGVMovies = (GridView) getView().findViewById(R.id.gvMovies);
        // set the MoviesAdapter to GridView
        mGVMovies.setAdapter(mAdapter);

        mGVMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);

                mPosition = position;

                if (cursor != null) {

                    ((Callback) getActivity())
                            .onGridItemSelected(
                                    MovieContract.MovieEntry.buildMovieUri(cursor.getLong(Utils.MovieColumnsIndex.COL_MOVIE_ID)),
                                    view.findViewById(R.id.iv_movie_poster)
                            );
                }

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The gridview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // saving selected position
        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String filterBy = prefs.getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_value_popularity));

        Uri filterMoviesUri = MovieContract.MovieEntry.buildMoviesUri(filterBy);

        return new CursorLoader(getActivity(),
                filterMoviesUri,
                Utils.MOVIES_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.e(MovieListFragment.class.getSimpleName(), " cursor data count : " + data.getCount());
        mAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION) {
            mGVMovies.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(getActivity()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (getString(R.string.pref_sort_key).equals(key)) {
            mPosition = 0;
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
        }
    }
}
