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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rohanr.moviedb.Adapter.ReviewAdapter;
import com.rohanr.moviedb.Adapter.TrailerVideoAdapter;
import com.rohanr.moviedb.Entity.Review;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
    Button markFav;
    Context parent;
    ArrayList<Trailers> trailersList;
    ArrayList<Review> reviewsList;
    RecyclerView recyclerView, recyclerViewReview;
    SQLiteDatabase db;

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

        new LongOperation().execute("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class LongOperation extends AsyncTask<String, Integer, JSONObject>{

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
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
                response = null;
            }
            return response;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Log.d("movieDb", "reached in post execute");
            try{
                if(result != null){
                    title.setText(result.getString("title"));
                    rating.setText(result.getString("vote_average") + "/10");
                    runTime.setText(result.get("runtime").toString() + " mins");
                    year.setText(result.get("release_date").toString());
                    desc.setText(result.getString("overview"));
                    Picasso.with(parent).load("http://image.tmdb.org/t/p/" + getString(R.string.default_poster_size) + result.getString("poster_path")).into(img);
                    new TrailersData().execute("");
                }

            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class TrailersData extends AsyncTask<String, Integer, JSONObject> {

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
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
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

                    mAdapter = new TrailerVideoAdapter(parent, trailersList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setAdapter(mAdapter);

                    new ReviewOperation().execute("");

                }
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private class ReviewOperation extends AsyncTask<String, Integer, JSONObject>{

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
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                bufferedReader.close();
                urlConnection.disconnect();
                response = (JSONObject) new JSONTokener(stringBuilder.toString()).nextValue();
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







