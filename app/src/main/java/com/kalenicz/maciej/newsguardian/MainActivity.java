package com.kalenicz.maciej.newsguardian;


import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final String API_URL = "http://content.guardianapis.com/search?";
    public static final String API_SHOW_BYLINE_TRAILTEXT = "show-fields=byline,trailText";
    public static final String API_KEY = "test";
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<HashMap<String, String>> newsList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsList = new ArrayList<>();

        NewsAsyncTask task = new NewsAsyncTask();
        task.execute();
    }

    private class NewsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            URL url = createUrl(API_URL + API_SHOW_BYLINE_TRAILTEXT + "&api-key=" + API_KEY);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }
            extractFromJson(jsonResponse);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            recyclerView = findViewById(R.id.list_recycler_view);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new NewsAdapter(newsList);
            recyclerView.setAdapter(adapter);
        }
    }

    private String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e("Error log", "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    private News extractFromJson(String newsJSON) {

        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = newsObject.getJSONArray("results");


            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentNews = resultsArray.getJSONObject(i);
                String section = currentNews.getString("sectionName");
                String headline = currentNews.getString("webTitle");
                String time ;
                String rawTime;
                if(currentNews.has("webPublicationDate")) {
                    rawTime = currentNews.getString("webPublicationDate"); // format in the JSON response
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // convert the String into Date
                    try {
                    Date date = format.parse(rawTime);
                    time = (String) android.text.format.DateFormat.format("MMM" + " " + "dd" + ", " + "HH:mm", date); // format the date and cast it to String again
                    } catch (ParseException exc) {
                        Log.e("Error:", "An exception was encountered while trying to parse a date " + exc);
                        time= "";
                    }

                } else {
                    time= "";
                }

                String description = currentNews.getJSONObject("fields").getString("trailText");
                String author;
                if (currentNews.getJSONObject("fields").has("byline")) {
                    author = "by " + currentNews.getJSONObject("fields").getString("byline");
                } else {
                    author = "no author";
                }

                HashMap<String, String> newsElement = new HashMap<>();
                newsElement.put("headline", headline);
                newsElement.put("author", author);
                newsElement.put("time", time);
                newsElement.put("section", section);
                newsElement.put("description", description);

                newsList.add(newsElement);
            }
        } catch (JSONException e) {
            Log.e("Error LOG", "Problem parsing the JSON results", e);
        }
        return null;
    }
}

