package com.kalenicz.maciej.newsguardian;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

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

public class MainActivity extends AppCompatActivity {

    public static final String API_URL = "http://content.guardianapis.com/search?";
    public static final String API_SHOW_BYLINE = "show-fields=byline";
    public static final String API_KEY = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NewsAsyncTask task = new NewsAsyncTask();
        task.execute();
    }

    private void updateUi(News news) {
        TextView section = findViewById(R.id.section);
        section.setText(news.getSection());

        TextView title = findViewById(R.id.title);
        title.setText(news.getHeadline());

        TextView time = findViewById(R.id.time);
        time.setText(news.getTimePublished());

        TextView author = findViewById(R.id.author);
        author.setText(news.getAuthor());
    }

    private class NewsAsyncTask extends AsyncTask<URL, Void, News> {

        @Override
        protected News doInBackground(URL... urls) {
            URL url = createUrl(API_URL + API_SHOW_BYLINE+"&api-key=" + API_KEY);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }
            News news = extractFromJson(jsonResponse);
            return news;
        }

        @Override
        protected void onPostExecute(News news) {
            if (news == null) {
                return;
            }

            updateUi(news);
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
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
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

            // If there are results in the features array
            if (resultsArray.length() > 0) {
                // Extract out the first feature (which is an earthquake)
                JSONObject currentNews = resultsArray.getJSONObject(0);
                String section = currentNews.getString("sectionName");
                String headline = currentNews.getString("webTitle");
                String time = currentNews.getString("webPublicationDate");

                String author;
                if(currentNews.getJSONObject("fields").has("byline")){
                    author = "by " + currentNews.getJSONObject("fields").getString("byline");
                } else {
                    author = "no author";
                }

                return new News(headline, author, time, "test", "test", section);
            }
        } catch (JSONException e) {
            Log.e("Error LOG", "Problem parsing the JSON results", e);
        }
        return null;
    }
}

