package com.rohanr.moviedb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.rohanr.moviedb.Adapter.MovieListAdapter;
import com.rohanr.moviedb.Entity.MovieData;
import com.rohanr.moviedb.MyUtil.UrlConnections;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    GridView myGrid;
    ArrayList<MovieData> movieList;
    Context mainContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainContext = this;
        myGrid = (GridView) findViewById(R.id.gridView);
        StringBuilder urlString = new StringBuilder();
        urlString.append(getString(R.string.base_url));
        urlString.append(getString(R.string.discover_movie)).append("?");
        urlString.append(getString(R.string.moviedb_api_key));
        new GetMovieList().execute(urlString.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        StringBuilder urlString = new StringBuilder();
        urlString.append(getString(R.string.base_url));
        urlString.append(getString(R.string.movie));

        if (id == R.id.filter_now_playing) {
            urlString.append(getString(R.string.now_playing_movies));
            setTitle(R.string.menu_now_playing_movies);
        } else if(id == R.id.filter_top_rated){
            urlString.append(getString(R.string.top_rated_movies));
            setTitle(R.string.menu_top_rated_movies);
        } else if(id == R.id.filter_popular){
            urlString.append(getString(R.string.popular_movies));
            setTitle(R.string.menu_popular_movies);
        } else if(id == R.id.filter_upcoming){
            urlString.append(getString(R.string.upcoming_movies));
            setTitle(R.string.menu_upcoming_movies);
        }

        urlString.append("?");
        urlString.append(getString(R.string.moviedb_api_key));

        new GetMovieList().execute(urlString.toString());

        return super.onOptionsItemSelected(item);
    }




    private class GetMovieList extends AsyncTask<String, Integer, JSONObject> implements AdapterView.OnItemClickListener {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONObject doInBackground(String... params) {
            JSONObject response;
            String urlString = params[0];
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
                    movieList = new ArrayList<MovieData>();
                    JSONArray jsonArray = result.optJSONArray("results");
                    for(int i=0; i<jsonArray.length(); i++){
                        JSONObject obj = jsonArray.getJSONObject(i);
                        Log.d("movieDb", obj.getString("title") + " " + obj.getString("poster_path") + " " + obj.getInt("id"));
                        MovieData md = new MovieData("http://image.tmdb.org/t/p/" + getString(R.string.default_poster_size) + obj.getString("poster_path"), obj.getString("title"), obj.getInt("id"));
                        movieList.add(md);
                    }
                }
                if(movieList.size() > 0){

                    myGrid.setAdapter(new MovieListAdapter(movieList, mainContext));
                    myGrid.setOnItemClickListener(this);
                }
            } catch (Exception e){
                Log.e("movieDb", e.getMessage());
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(mainContext, MovieDetails.class);
            intent.putExtra("movieId", movieList.get(position).getMovieId());
            intent.putExtra("movieName",movieList.get(position).getTitle().toString());
            startActivity(intent);
        }
    }

}
