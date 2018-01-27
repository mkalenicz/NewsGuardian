package com.kalenicz.maciej.newsguardian;

import android.content.AsyncTaskLoader;
import android.content.Context;

import android.text.TextUtils;
import android.util.Log;

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

import static com.kalenicz.maciej.newsguardian.BuildConfig.NEWS_GUARDIAN_API_KEY;
import static com.kalenicz.maciej.newsguardian.MainActivity.URI_URL;

public class NewsLoader extends AsyncTaskLoader<ArrayList<HashMap<String, String>>> {
    public static final String API_URL = "http://content.guardianapis.com/search?show-fields=byline,trailText&api-key=" + NEWS_GUARDIAN_API_KEY;
    public static final int SUCCESS_CODE = 200;
    public boolean serverResponse;
    private ArrayList<HashMap<String, String>> newsList;
    private HashMap<String, String> newsElement;

    private String mUrl;

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public ArrayList<HashMap<String, String>> loadInBackground() {

        if (mUrl == null) {
            return null;
        }
        URL url = createUrl(URI_URL);
        Log.i("NewsLoader","URI_URL is: " +URI_URL);
//        URL url = createUrl();
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("NewLoader", "Problem with loadInBackground method", e);
        }
        extractFromJson(jsonResponse);
        return newsList;
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

            if (urlConnection.getResponseCode() == SUCCESS_CODE) {
                serverResponse = true;
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                serverResponse = false;
                Log.e("NewsLoader", "Error response code" + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("NewLoader", "Problem retrieving JSON results.", e);
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

    private ArrayList<HashMap<String, String>> extractFromJson(String newsJSON) {

        newsList = new ArrayList<>();
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }
        try {
            JSONObject baseJsonResponse = new JSONObject(newsJSON);
            JSONObject newsObject = baseJsonResponse.getJSONObject("response");
            JSONArray resultsArray = newsObject.getJSONArray("results");


            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject currentNews = resultsArray.getJSONObject(i);
                String section = currentNews.getString("sectionName");
                String headline = currentNews.getString("webTitle");
                String webUrl = currentNews.getString("webUrl");
                String time;
                String rawTime;
                if (currentNews.has("webPublicationDate")) {
                    rawTime = currentNews.getString("webPublicationDate"); // format in the JSON response
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // convert the String into Date
                    try {
                        Date date = format.parse(rawTime);
                        time = (String) android.text.format.DateFormat.format("MMM" + " " + "dd" + ", " + "HH:mm", date); // format the date and cast it to String again
                    } catch (ParseException exc) {
                        Log.e("Error:", "An exception was encountered while trying to parse a date " + exc);
                        time = "";
                    }

                } else {
                    time = "";
                }

                String description = currentNews.getJSONObject("fields").getString("trailText");
                String author;
                if (currentNews.getJSONObject("fields").has("byline")) {
                    author = "by " + currentNews.getJSONObject("fields").getString("byline");
                } else {
                    author = "no author";
                }

                newsElement = new HashMap<>();
                newsElement.put("headline", headline);
                newsElement.put("author", author);
                newsElement.put("time", time);
                newsElement.put("section", section);
                newsElement.put("description", description);
                newsElement.put("webUrl", webUrl);

                newsList.add(newsElement);
            }

        } catch (JSONException e) {
            Log.e("Error LOG", "Problem parsing the JSON results", e);
        }
        return newsList;
    }

}
