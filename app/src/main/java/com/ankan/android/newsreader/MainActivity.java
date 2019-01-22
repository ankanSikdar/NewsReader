package com.ankan.android.newsreader;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    static ArrayList<String> idArrayList;
    static ArrayList<String> titleArrayList;
    static ArrayList<String> urlArrayList;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        listView = findViewById(R.id.listView);
        idArrayList = new ArrayList<>();
        titleArrayList = new ArrayList<>();
        urlArrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, titleArrayList);

        IdDownloadTask downloadTask = new IdDownloadTask();
        downloadTask.execute("https://hacker-news.firebaseio.com/v0/topstories.json");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WebViewActivity.class);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });

    }

    public class IdDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection httpURLConnection = null;
            String result = "";

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char currentChar = (char) data;
                    result += currentChar;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                int numOfNews = 20; // no. of news ids to get
                for(int i = 0; i < numOfNews; i++) {
                    idArrayList.add(jsonArray.get(i).toString());
                }
                listView.setAdapter(arrayAdapter);
                for (int i = 0; i < idArrayList.size(); i++) {
                    NewsDownloadTask newsDownloadTask = new NewsDownloadTask();
                    newsDownloadTask.execute(idArrayList.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class NewsDownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection httpURLConnection = null;
            String result = "";

            try {
                url = new URL("https://hacker-news.firebaseio.com/v0/item/" + urls[0] + ".json");
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream in = httpURLConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while (data != -1) {
                    char currentChar = (char) data;
                    result += currentChar;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String title = jsonObject.getString("title");
                String url = jsonObject.getString("url");
                titleArrayList.add(title);
                urlArrayList.add(url);
                arrayAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
