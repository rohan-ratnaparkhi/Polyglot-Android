package com.rohanr.moviedb;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rohanr.moviedb.Adapter.ReviewAdapter;
import com.rohanr.moviedb.Adapter.TrailerVideoAdapter;
import com.rohanr.moviedb.Entity.Review;
import com.rohanr.moviedb.MyUtil.UrlConnections;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import com.rohanr.moviedb.Entity.Trailers;


public class MovieDetails extends ActionBarActivity {

    String movieId;
    TextView title;
    ImageView img;
    TextView desc;
    TextView runTime;
    TextView rating;
    TextView year;
    TextView favorite;
    TextView trailerText;
    TextView reviewText;
    Button markFav;
    Context parent;
    ArrayList<Trailers> trailersList;
    ArrayList<Review> reviewsList;
    RecyclerView recyclerView, recyclerViewReview;
    SQLiteDatabase db;
    View trailerSeparator;
    View reviewSeparator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        parent = this;
        recyclerView = (RecyclerView) findViewById(R.id.rv_trailers);

        recyclerViewReview = (RecyclerView) findViewById(R.id.rv_reviews);

        Bundle bundle = getIntent().getExtras();
        title = (TextView) findViewById(R.id.dtl_title);
        desc = (TextView) findViewById(R.id.dtl_desc);
        runTime = (TextView) findViewById(R.id.dtl_run_time);
        rating = (TextView) findViewById(R.id.dtl_rating);
        img = (ImageView) findViewById(R.id.dtl_image);
        year = (TextView) findViewById(R.id.dtl_year);
        movieId = bundle.get("movieId").toString();
        favorite = (TextView) findViewById(R.id.tv_fav);
        markFav = (Button) findViewById(R.id.btn_fav);
        trailerText = (TextView) findViewById(R.id.tv_trailers);
        reviewText = (TextView) findViewById(R.id.tv_reviews);
        trailerSeparator = findViewById(R.id.trailer_separator);
        reviewSeparator = findViewById(R.id.review_separator);

        db = openOrCreateDatabase("movieDbApp", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS FavoriteMovie(id INTEGER);");

        Cursor resultSet = db.rawQuery("Select * from FavoriteMovie where id=" + movieId,null);
        Log.d("movieDb","records in db: " + resultSet.getCount());
        if(resultSet.getCount() > 0){
            favorite.setAlpha(1);
        } else {
            markFav.setAlpha(1);
        }

        markFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.execSQL("INSERT INTO FavoriteMovie(id) VALUES("+ movieId +")");
                markFav.setAlpha(0);
                favorite.setAlpha(1);
            }
        });

        resultSet.close();

        new GetMovieDetails().execute("");

    }

    private class GetMovieDetails extends AsyncTask<String, Integer, JSONObject>{

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            int i = 0;
            publishProgress(i);
            StringBuilder urlString = new StringBuilder();
            urlString.append(getString(R.string.base_url));
            urlString.append(getString(R.string.find_movie).replace("id",movieId)).append("?");
            urlString.append(getString(R.string.moviedb_api_key));
            try{
                URL url = new URL(urlString.toString());
                response = UrlConnections.getData(url);
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
//            Log.d("movieDb", "reached in post execute");
            try{
                if(result != null){
                    title.setText(result.getString("title"));
                    rating.setText(result.getString("vote_average") + "/10");
                    runTime.setText(result.get("runtime").toString() + " mins");
                    year.setText(result.get("release_date").toString());
                    desc.setText(result.getString("overview"));
                    Picasso.with(parent).load("http://image.tmdb.org/t/p/" + getString(R.string.default_poster_size) + result.getString("poster_path")).into(img);
                    new GetMovieTrailers().execute("");
                }

            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class GetMovieTrailers extends AsyncTask<String, Integer, JSONObject> {

        private TrailerVideoAdapter mAdapter;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            StringBuilder urlString = new StringBuilder();
            urlString.append(getString(R.string.base_url));
            urlString.append(getString(R.string.find_movie).replace("id",movieId));
            urlString.append(getString(R.string.get_videos)).append("?");
            urlString.append(getString(R.string.moviedb_api_key));
            try{
                URL url = new URL(urlString.toString());
                response = UrlConnections.getData(url);
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            trailersList = new ArrayList<Trailers>();
            Log.d("movieDb", "reached in post execute");
            try{
                if(result != null){
                    JSONArray trailers = result.getJSONArray("results");
                    for(int i=0;i<trailers.length();i++){
                        JSONObject obj = trailers.getJSONObject(i);
                        Trailers tr = new Trailers();
                        tr.setName(obj.getString("name"));
                        tr.setSite(obj.getString("site"));
                        tr.setType(obj.getString("type"));
                        tr.setKey(obj.getString("key"));
                        trailersList.add(tr);
                    }
                }
                if(trailersList.size() > 0){
                    trailerSeparator.setAlpha(1);
                    trailerText.setAlpha(1);
                    mAdapter = new TrailerVideoAdapter(parent, trailersList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);

                    new GetMovieReviews().execute("");

                }
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class GetMovieReviews extends AsyncTask<String, Integer, JSONObject>{

        private  ReviewAdapter mAdapter;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            int i = 0;
            publishProgress(i);
            StringBuilder urlString = new StringBuilder();
            urlString.append(getString(R.string.base_url));
            urlString.append(getString(R.string.find_movie).replace("id",movieId));
            urlString.append(getString(R.string.get_reviews)).append("?");
            urlString.append(getString(R.string.moviedb_api_key));
            try{
                URL url = new URL(urlString.toString());
                response = UrlConnections.getData(url);
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            reviewsList = new ArrayList<Review>();
            Log.d("movieDb", "reached in post execute for Reviews");
            try{
                if(result != null){
                    JSONArray reviews = result.getJSONArray("results");
                    for(int i=0;i<reviews.length();i++){
                        JSONObject obj = reviews.getJSONObject(i);
                        Review rev = new Review();
                        rev.setReviewer(obj.getString("author"));
                        rev.setComment(obj.getString("content"));
                        reviewsList.add(rev);
                    }
                }
                if(reviewsList.size() > 0){
                    reviewSeparator.setAlpha(1);
                    reviewText.setAlpha(1);
                    mAdapter = new ReviewAdapter(parent, reviewsList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerViewReview.setLayoutManager(mLayoutManager);
                    recyclerViewReview.setAdapter(mAdapter);

                }
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

}







