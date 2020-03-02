package com.example.musixmatchtracksearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SeekBar seek;
    private int seekLimMin = 5;
    public int seekData;
    private TextView limitTextView;
    private Button btnSearch;
    private EditText edSearchKey;
    private static RadioGroup rgSongRating;
    private static RadioButton rbArtist;
    private static RadioButton rbTrack;
    private String stringChioice = "s_track_rating";
    private ProgressDialog progressDialog;
    private MusicAdapter musicAdapter;
    private ListView myListView;
    static String MUSIC_KEY = "MyMusic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        seek = findViewById(R.id.seekBarLim);
        seek.setMax(20);
        limitTextView = findViewById(R.id.tvLimitSet);
        limitTextView.setText(String.valueOf(seekLimMin));
        btnSearch = findViewById(R.id.btnSearch);
        edSearchKey = findViewById(R.id.etSearchKey);
        rgSongRating = findViewById(R.id.radioGroup);
        rbArtist = findViewById(R.id.s_artist_rating);
        rbTrack = findViewById(R.id.s_track_rating);
        myListView = findViewById(R.id.listViewId);
        //Log.d("Radio", "Clicked: " + rbChoice);

        /*rgSongRating.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rbChoice = findViewById(checkedId);
                Log.d("Radio", "Clicked: " +  rbChoice);
            }
        });*/

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isConnected()) {
                    if (edSearchKey.getText().toString().trim().length() == 0) {
                        edSearchKey.setError("Please enter Song name");
                    } else {
                        Log.d("demo", edSearchKey.getText().toString());
                        Log.d("demo", limitTextView.getText().toString());

                        /*if (rbChoice.getText() == null || rbChoice.getText() == "Track rating") {
                            stringChioice = "s_track_rating";
                        } else if(rbChoice.getText() == "Artist rating") {
                            stringChioice = "s_artist_rating";
                        }*/
                        if(rbTrack.isChecked()){
                            stringChioice = "s_track_rating";
                        }else{
                            stringChioice = "s_artist_rating";
                        }
                        //Start Async Task after adding parameters to the URL
                        RequestParams params = new RequestParams();
                        params.addParameter("q", edSearchKey.getText().toString())
                                .addParameter("page_size", limitTextView.getText().toString())
                                        .addParameter(stringChioice, "desc")
                                                .addParameter("apikey", "39cb93aaae1a8d07007efec5e7e02017");

                        new GetJsonDataUsingGetParams(params).execute("https://api.musixmatch.com/ws/1.1/track.search");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection!", Toast.LENGTH_LONG).show();
                }

                //myListView.setVisibility(ListView.VISIBLE);
            }
        });


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                seekData = progress + seekLimMin;
                limitTextView.setText(String.valueOf(seekData));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
    private class GetJsonDataUsingGetParams extends AsyncTask<String, Void, ArrayList<Tracks>> {

        RequestParams mParams;

        public GetJsonDataUsingGetParams(RequestParams params){
            mParams = params;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Fetching Music...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Tracks> doInBackground(String... strings) {

            HttpURLConnection conn = null;
            BufferedReader br = null;
            String json = null;
            ArrayList<Tracks> result = new ArrayList<>();

            try {

                URL url = new URL(mParams.getEncodedUrl(strings[0]));
                Log.d("demo", "rul : " + url);
                conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){

                    json = IOUtils.toString(conn.getInputStream(),"UTF8");

                    JSONObject root =new JSONObject(json);
                    JSONObject message =new JSONObject(json);
                    JSONObject body =new JSONObject(json);
                    Log.d("Root", "Root: " + root);
                    message = root.getJSONObject("message");
                    body = message.getJSONObject("body");
                    JSONArray musicArray = body.getJSONArray("track_list");
                    Log.d("demo", "body : " + musicArray.toString());
                    for (int i = 0; i < musicArray.length(); i++) {

                        JSONObject tracksList = musicArray.getJSONObject(i);
                        JSONObject musicJson = tracksList.getJSONObject("track");

                        Tracks music = new Tracks();
                        music.track_name = musicJson.getString("track_name");
                        music.artist_name = musicJson.getString("artist_name");
                        music.album_name = musicJson.getString("album_name");
                        music.track_share_url = musicJson.getString("track_share_url");
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
                        String dt = musicJson.getString("updated_time");
                        Date myDate = formatter.parse(dt);
                        SimpleDateFormat newFormat = new SimpleDateFormat("MM-DD-yyyy");
                        String finalString = newFormat.format(myDate);

                        //music.date = musicJson.getString("updated_time");

                        music.date = finalString;

                        Log.d("Demo" ,"track_name " + music.track_name);
                        Log.d("Demo" ,"date" + music.date);
                        /*music.trackImage = musicJson.getString("artworkUrl100");
                        music.albumName = musicJson.getString("collectionName");
                        music.albumPrice = musicJson.getString("collectionPrice");
                        music.genre = musicJson.getString("primaryGenreName");*/

                        result.add(music);
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return result;
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }

        @Override
        protected void onPostExecute(final ArrayList<Tracks> res) {

            progressDialog.dismiss();

            if(res.size() > 0){
                Log.d("demo", res.toString());

                musicAdapter = new MusicAdapter(MainActivity.this, R.layout.list_item_view, res);
                myListView.setAdapter(musicAdapter);
                myListView.setVisibility(View.VISIBLE);
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("demo", "Clicked: " + res.get(position));

                        String LyricsURL = res.get(position).track_share_url;
                        Log.d("Demo", "URL" + LyricsURL );
                        Intent newIntent = new Intent(Intent.ACTION_VIEW);
                        newIntent.setData(Uri.parse(LyricsURL));
                        startActivity(newIntent);
                    }
                });

            } else{
                Log.d("demo", "No data received");
            }
        }

    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected() ||
                (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                        && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            return false;
        }
        return true;
    }
}
