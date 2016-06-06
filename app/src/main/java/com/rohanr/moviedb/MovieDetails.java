package com.rohanr.moviedb;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MovieDetails extends ActionBarActivity {

    String movieId;
    TextView title;
    ImageView img;
    TextView desc;
    TextView runTime;
    TextView rating;
    TextView year;
    Context parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        Bundle bundle = getIntent().getExtras();
        title = (TextView) findViewById(R.id.dtl_title);
        desc = (TextView) findViewById(R.id.dtl_desc);
        runTime = (TextView) findViewById(R.id.dtl_run_time);
        rating = (TextView) findViewById(R.id.dtl_rating);
        img = (ImageView) findViewById(R.id.dtl_image);
        year = (TextView) findViewById(R.id.dtl_year);
        movieId = bundle.get("movieId").toString();
        parent = this;
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
//            progressBar.setVisibility(View.VISIBLE);
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


