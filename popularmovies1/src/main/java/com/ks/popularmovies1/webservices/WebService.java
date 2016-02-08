package com.ks.popularmovies1.webservices;

import com.ks.popularmovies1.models.DiscoverResult;
import com.ks.popularmovies1.models.Movie;
import com.ks.popularmovies1.models.Review;
import com.ks.popularmovies1.models.TrailerResult;
import com.ks.popularmovies1.utils.Utils;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by karn.shah on 26-12-2015.
 */
public class WebService {

    public static WebServiceEndPoints setAdapter() {
        return new RestAdapter.Builder()
                .setEndpoint(Utils.END_POINT_PATH)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestInterceptor.RequestFacade request) {
                        // We expect a json response from API
                        request.addHeader("Accept", "application/json");

                        // "vote_average" is not a true measure of votes, a lot of movies have
                        // high vote_average value but the number of vote are small so the result
                        // of high vote_average are a unknown movies.
                        request.addQueryParam("vote_count.gte", "1000"); // get significant result

                        // we add API Key value as parameter
                        request.addQueryParam("api_key", Utils.API_KEY);
                    }
                })
                        .setLogLevel(RestAdapter.LogLevel.FULL) // only by debugging purpose
                .build()
                .create(WebServiceEndPoints.class);
    }

   public interface WebServiceEndPoints {

        /**
         * Discover 20 top most popular or votes movies
         *
         * @param sortBy R.string.pref_sort_value_popularity or R.string.pref_sort_value_rate
         */
        @GET("/discover/movie")
        void discover(@Query("sort_by") String sortBy, Callback<DiscoverResult<Movie>> cb);

        /**
         * Get Movie Detail from TMDb API using a Http Request
         * @param movieId Movie ID value
         */
        @GET("/movie/{id}")
        void movieDetail(@Path("id") int movieId, Callback<Movie> cb);

       /**
        * Get videos (trailers) by movie id
        * @param movieId
        * @param callback
        */
       @GET("/movie/{id}/trailers")
       void movieTrailers(@Path("id") long movieId, Callback<TrailerResult> callback);

       /**
        * Get reviews by movie id
        * @param movieId
        * @param callback
        */
       @GET("/movie/{id}/reviews")
       void movieReviews(@Path("id") long movieId, Callback<DiscoverResult<Review>> callback);
    }
}
