package com.ks.popularmovies1.fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ks.popularmovies1.R;
import com.ks.popularmovies1.data.MovieContract;
import com.ks.popularmovies1.models.DiscoverResult;
import com.ks.popularmovies1.models.Movie;
import com.ks.popularmovies1.models.Review;
import com.ks.popularmovies1.models.Trailer;
import com.ks.popularmovies1.models.TrailerResult;
import com.ks.popularmovies1.utils.PicassoBigCache;
import com.ks.popularmovies1.utils.Utils;
import com.ks.popularmovies1.webservices.WebService;
import com.squareup.picasso.Picasso;

import java.util.Vector;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by karn.shah on 27-12-2015.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

   private Movie movie;

    public static String DETAIL_URI_KEY = "DETAIL_URI_KEY";
    private Uri mUri;
    private long mMovieId;
    private boolean mTwoPane;
    private WebService.WebServiceEndPoints mService;

    private static final int DETAIL_LOADER = 1;
    private static final int TRAILERS_LOADER = 2;
    private static final int REVIEWS_LOADER = 3;

    private Toolbar mToolbar;

    private CoordinatorLayout mClContainer;

    private ShareActionProvider mShareActionProvider;
    private String shareContent = null;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mToolbarImage;

    private CardView mCvHeader;
    private CardView mCvOverview;
    private CardView mCvTrailers;
    private CardView mCvReviews;

    private ImageView mPosterView;
    private TextView mDateReleaseView;
    private TextView mVoteAverageView;
    private TextView mOverviewView;
    private LinearLayout mReviewsView;
    private LinearLayout mTrailersView;

    private FloatingActionButton mBtFavoriteView;
    private boolean mIsFavorite = false;

    private Picasso p;

    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Utils.MOVIE_DETAIL_KEY)) {
            movie = (Movie) intent.getParcelableExtra(Utils.MOVIE_DETAIL_KEY);
        }
        mService = WebService.setAdapter();
        p = PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity());
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

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI_KEY);
            mMovieId = Long.parseLong(mUri.getLastPathSegment());
        }


        mClContainer = (CoordinatorLayout) getView().findViewById(R.id.clContainer);

        mCollapsingToolbarLayout = ((CollapsingToolbarLayout) getView().findViewById(R.id.collapsingToolbarLayout));
        mToolbarImage = (ImageView) getView().findViewById(R.id.ivBackdrop);

        mTwoPane = getActivity().findViewById(R.id.movie_detail_container) == null;

        if(!mTwoPane) {
            mToolbar = (Toolbar) getView().findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mCvHeader = (CardView) getView().findViewById(R.id.cvMovieHeader);
        mCvOverview = (CardView) getView().findViewById(R.id.cvMovieOverview);
        mCvTrailers = (CardView) getView().findViewById(R.id.cvMovieTrailers);
        mCvReviews = (CardView) getView().findViewById(R.id.cvMovieReviews);

        mOverviewView = (TextView) getView().findViewById(R.id.tvOverview);
        mDateReleaseView = (TextView) getView().findViewById(R.id.tvDateRelease);
        mVoteAverageView = (TextView) getView().findViewById(R.id.tvVoteAverage);
        mPosterView = (ImageView) getView().findViewById(R.id.ivDetailPoster);
        mReviewsView = (LinearLayout) getView().findViewById(R.id.llReviewContainer);
        mTrailersView = (LinearLayout) getView().findViewById(R.id.llTrailerContainer);

        mBtFavoriteView = (FloatingActionButton) getView().findViewById(R.id.fabBtn);

        mBtFavoriteView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues favValues = new ContentValues();
                favValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, (mIsFavorite ? 0 : 1));

                int updateMovies = getActivity().getContentResolver().update(
                        MovieContract.MovieEntry.CONTENT_URI,
                        favValues,
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = ? ",
                        new String[] { Long.toString(mMovieId) });

                if (updateMovies > 0) {
                    // toggle favorite
                    mIsFavorite = !mIsFavorite;
                }
            }
        });
    }

    public static class ReviewViewHolder {

        public final TextView author;
        public final TextView content;

        public ReviewViewHolder(View view) {
            author = (TextView) view.findViewById(R.id.tv_review_author);
            content = (TextView) view.findViewById(R.id.tv_review_content);
        }
    }

    public static class TrailerViewHolder {

        public final TextView name;
        public final ImageView image;
        public final Button button;

        public TrailerViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.tv_trailer_name);
            image = (ImageView) view.findViewById(R.id.iv_trailer_image);
            button = (Button) view.findViewById(R.id.bt_trailer);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        postponeTransition();

        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null && mMovieId > -1) {
            if (id == DETAIL_LOADER) {
                // load movie
                return new CursorLoader(
                        getActivity(),
                        mUri,
                        Utils.MOVIE_DETAIL_COLUMNS,
                        null,
                        null,
                        null
                );
            } else if (id == REVIEWS_LOADER) {
                // load reviews
                return new CursorLoader(
                        getActivity(),
                        MovieContract.ReviewEntry.CONTENT_URI,
                        Utils.REVIEW_COLUMNS,
                        MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(mMovieId)},
                        null
                );
            } else if (id == TRAILERS_LOADER) {
                // load trailers
                return new CursorLoader(
                        getActivity(),
                        MovieContract.TrailerEntry.CONTENT_URI,
                        Utils.TRAILER_COLUMNS,
                        MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ",
                        new String[]{Long.toString(mMovieId)},
                        MovieContract.TrailerEntry.COLUMN_INDEX + " asc"
                );
            }
        }

        return null;
    }

    private void postponeTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().postponeEnterTransition();
    }

    private void initTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getActivity().startPostponedEnterTransition();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, final Cursor data) {

        if (loader == null)
            return;

        if (loader.getId() == DETAIL_LOADER) {

            if (data != null && data.moveToFirst()) {

                mDateReleaseView.setText(getString(R.string.movie_detail_release_date) + ": " + data.getString(Utils.MovieColumnIndex.COL_RELEASE_DATE));
                mVoteAverageView.setText(data.getFloat(Utils.MovieColumnIndex.COL_VOTE_AVERAGE) + "/10");
                mOverviewView.setText(data.getString(Utils.MovieColumnIndex.COL_OVERVIEW));

                mIsFavorite = data.getInt(Utils.MovieColumnIndex.COL_FAVORITE) > 0;

                if (mIsFavorite) {
                    mBtFavoriteView.setImageResource(R.drawable.ic_favorite_white);
                } else {
                    mBtFavoriteView.setImageResource(R.drawable.ic_favorite_border_white);
                }

                mCollapsingToolbarLayout.setTitle(data.getString(Utils.MovieColumnIndex.COL_ORIGINAL_TITLE));

                if(mTwoPane) {
                    mCollapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.PopularMovieCollapsedAppBar);
                }

                mClContainer.setVisibility(View.VISIBLE);
                mCvHeader.setVisibility(View.VISIBLE);
                mCvOverview.setVisibility(View.VISIBLE);

                // load the backdrop image
                p.load(Utils.IMG_END_POINT + "w780" + data.getString(Utils.MovieColumnIndex.COL_BACKDROP_IMAGE_PATH))
                        // put the result image in poster ImageView
                        .into(mToolbarImage);

                // load the poster image
                p.load(Utils.IMG_END_POINT + "w185" + data.getString(Utils.MovieColumnIndex.COL_POSTER_IMAGE_PATH))
                        // if the image don't exist we use a default drawable
                        .error(R.drawable.poster_missing)
                                // put the result image in poster ImageView
                        .into(mPosterView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                initTransition();
                            }

                            @Override
                            public void onError() {
                                initTransition();
                            }
                        });
            }

        } else if (loader.getId() == REVIEWS_LOADER) {

            if (data != null && data.moveToFirst()) {

                mReviewsView.removeAllViews();

                do {
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_review, mReviewsView, false);
                    final ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);
                    reviewViewHolder.author.setText(data.getString(Utils.ReviewColumnIndex.COL_AUTHOR));
                    reviewViewHolder.content.setText(data.getString(Utils.ReviewColumnIndex.COL_CONTENT));

                    view.setTag(reviewViewHolder);
                    mReviewsView.addView(view);
                } while (data.moveToNext());

                mCvReviews.setVisibility(View.VISIBLE);

            } else if (Utils.isNetworkConnected(getActivity())) {

                mService.movieReviews(mMovieId, new Callback<DiscoverResult<Review>>() {
                    @Override
                    public void success(final DiscoverResult<Review> result, Response response) {

                        if (result != null && result.results.size() > 0) {

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    Vector<ContentValues> reviews = new Vector<>(result.results.size());

                                    for (Review review : result.results) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review.getId());
                                        values.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
                                        values.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());
                                        values.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, mMovieId);

                                        reviews.add(values);
                                    }

                                    ContentValues[] cvArray = new ContentValues[reviews.size()];
                                    reviews.toArray(cvArray);

                                    Context ctx = getActivity();

                                    if (ctx != null)
                                        getActivity().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);

                                    return null;
                                }
                            }.execute();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });
            }

        } else if (loader.getId() == TRAILERS_LOADER) {

            if (data != null && data.moveToFirst()) {

                mTrailersView.removeAllViews();

                do {
                    if (shareContent == null)
                        shareContent = Utils.YOUTUBE_VIDEO_END_POINT + data.getString(Utils.TrailerColumnIndex.COL_SOURCE);

                    if (mShareActionProvider != null) {
                        mShareActionProvider.setShareIntent(createShareForecastIntent());
                    }

                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_trailor, mTrailersView, false);
                    TrailerViewHolder trailerViewHolder = new TrailerViewHolder(view);
                    trailerViewHolder.name.setText(data.getString(Utils.TrailerColumnIndex.COL_NAME));

                    p.load(Utils.YOUTUBE_IMAGE_END_POINT + data.getString(Utils.TrailerColumnIndex.COL_SOURCE) + "/default.jpg")
                            .error(R.drawable.movie_missing) // if the image don't exist we use a default drawable
                            .placeholder(R.drawable.movie_missing)
                            .into(trailerViewHolder.image); // put the result image in ImageView

                    trailerViewHolder.button.setOnClickListener(new View.OnClickListener() {

                        final Uri youtubeUri = Uri.parse(Utils.YOUTUBE_VIDEO_END_POINT + data.getString(Utils.TrailerColumnIndex.COL_SOURCE));

                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
                        }
                    });

                    view.setTag(trailerViewHolder);

                    mTrailersView.addView(view);
                } while (data.moveToNext());

                mCvTrailers.setVisibility(View.VISIBLE);
            } else if (Utils.isNetworkConnected(getActivity())){

                mService.movieTrailers(mMovieId, new Callback<TrailerResult>() {
                    @Override
                    public void success(final TrailerResult result, Response response) {

                        if (result != null && result.youtube.size() > 0) {

                            new AsyncTask<Void, Void, Void>() {
                                @Override
                                protected Void doInBackground(Void... params) {
                                    Vector<ContentValues> trailers = new Vector<>(result.youtube.size());

                                    for (Trailer trailer : result.youtube) {
                                        ContentValues values = new ContentValues();

                                        values.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, mMovieId);
                                        values.put(MovieContract.TrailerEntry.COLUMN_INDEX, result.youtube.indexOf(trailer));
                                        values.put(MovieContract.TrailerEntry.COLUMN_SOURCE, trailer.getSource());
                                        values.put(MovieContract.TrailerEntry.COLUMN_NAME, trailer.getName());

                                        trailers.add(values);
                                    }

                                    ContentValues[] cvArray = new ContentValues[trailers.size()];
                                    trailers.toArray(cvArray);

                                    Context ctx = getActivity();

                                    if (ctx != null)
                                        getActivity().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);

                                    return null;
                                }
                            }.execute();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });

            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == REVIEWS_LOADER)
            mReviewsView.removeAllViews();
        else if (loader.getId() == TRAILERS_LOADER)
            mTrailersView.removeAllViews();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.share, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (shareContent != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed(); //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
