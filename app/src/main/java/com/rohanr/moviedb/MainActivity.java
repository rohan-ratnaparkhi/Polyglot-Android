package com.rohanr.moviedb;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
        new LongOperation().execute("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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




    private class LongOperation extends AsyncTask<String, Integer, JSONObject> implements AdapterView.OnItemClickListener {

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
            urlString.append(getString(R.string.discover_movie)).append("?");
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
                    myGrid.setAdapter(new MyAdapter(movieList, mainContext));
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
//            Toast.makeText(mainContext, "name="+ movieList.get(position).title, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(mainContext, MovieDetails.class);
            intent.putExtra("movieId", movieList.get(position).movieId);
            intent.putExtra("movieName",movieList.get(position).title.toString());
            startActivity(intent);
        }
    }

}

class MovieData {
    String imageUrl;
    String title;
    Integer movieId;
    MovieData(String imageUrl, String title, Integer movieId){
        this.imageUrl = imageUrl;
        this.title = title;
        this.movieId = movieId;
    }
}

class MyAdapter extends BaseAdapter{

    ArrayList<MovieData> movies;
    Context context;

    MyAdapter(ArrayList<MovieData> movies, Context context){
        this.movies = movies;
        this.context = context;
    }

    class ViewHolder{
        ImageView myMovie;
        ViewHolder(View v){
            myMovie = (ImageView) v.findViewById(R.id.imageView);
        }
    }

    @Override
    public int getCount() {
        return this.movies.size();
    }

    @Override
    public Object getItem(int position) {
        return this.movies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.movies.get(position).movieId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;
        if(row == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.single_item, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Picasso.with(context).load(movies.get(position).imageUrl).into(holder.myMovie);
        holder.myMovie.setAdjustViewBounds(true);

        return row;
    }
}