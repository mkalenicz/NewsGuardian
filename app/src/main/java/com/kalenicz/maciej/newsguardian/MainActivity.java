package com.kalenicz.maciej.newsguardian;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static com.kalenicz.maciej.newsguardian.NewsLoader.API_URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    TextView noDataTextView;
    ImageView noDataImageView;
    ArrayList<HashMap<String, String>> newsList;
    Context context;
    private SwipeRefreshLayout swipeContainer;
    LoaderManager loaderManager;
    TextView emptyStateTextView;
    ImageView emptyStateImageView;
    TextView emptyStateTextView2;
    ConnectivityManager connMgr;
    NetworkInfo networkInfo;
    Uri.Builder uriBuilder;
    public static String URI_URL;
    private String mKeyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mKeyText = getText(R.string.settings_select_sections_key).toString();
        recyclerView = findViewById(R.id.list_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(new ArrayList<HashMap<String, String>>());
        recyclerView.setAdapter(adapter);
        emptyStateTextView = findViewById(R.id.empty_view);
        emptyStateImageView = findViewById(R.id.empty_view_img);
        emptyStateTextView2 = findViewById(R.id.empty_view2);
        noDataTextView = findViewById(R.id.no_data_news);
        noDataImageView = findViewById(R.id.no_data_news_img);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(1, null, this);

        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);


            // Update empty state with no connection error message
            emptyStateTextView.setText(R.string.no_internet_connection);
            emptyStateTextView2.setText(R.string.no_internet_description);
            emptyStateImageView.setImageResource(R.drawable.ic_cloud_off_black_24dp);

        }

        RecyclerView recyclerView = findViewById(R.id.list_recycler_view);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String url;
                        HashMap<String, String> newsModel = newsList.get(position);
                        url = newsModel.get("webUrl");
                        openWebPage(url);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        String url;
                        HashMap<String, String> newsModel = newsList.get(position);
                        url = newsModel.get("webUrl");
                        openWebPage(url);
                    }

                })
        );

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                networkInfo = connMgr.getActiveNetworkInfo();

                if (loaderManager != null) {
                    loaderManager.restartLoader(1, null, MainActivity.this);

                    if (networkInfo != null && networkInfo.isConnected()) {
                        hideNoInternet();

                        if (newsList == null) {
                            NewsLoader newsloader = new NewsLoader(MainActivity.this, API_URL);
                            if (newsloader.serverResponse == false && networkInfo != null && networkInfo.isConnected()) {
                                noDataTextView.setText(R.string.no_data_text);
                                noDataImageView.setImageResource(R.drawable.ic_cloud_computing_2);
                                noDataTextView.setVisibility(View.VISIBLE);
                                noDataImageView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            noDataTextView.setVisibility(View.INVISIBLE);
                            noDataImageView.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        showNoInternet();
                    }
                } else {
                    loaderManager = getLoaderManager();
                    loaderManager.initLoader(1, null, MainActivity.this);
                    if (networkInfo != null && networkInfo.isConnected()) {
                        hideNoInternet();
                    } else {
                        showNoInternet();
                    }
                }
                swipeContainer.setRefreshing(false);
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        final SharedPreferences prefs = getPrefs();
        final Set<String> values =
                prefs.getStringSet(mKeyText, null);

        if (values == null)
            return;
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNoInternet() {
        emptyStateTextView.setVisibility(View.VISIBLE);
        emptyStateTextView2.setVisibility(View.VISIBLE);
        emptyStateImageView.setVisibility(View.VISIBLE);
    }

    private void hideNoInternet() {
        emptyStateTextView.setVisibility(View.INVISIBLE);
        emptyStateTextView2.setVisibility(View.INVISIBLE);
        emptyStateImageView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int id, Bundle bundle) {
        uriSharedPrefs();
        return new NewsLoader(this, URI_URL);
    }

    private void uriSharedPrefs() {
        final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        Set<String> values = sharedPrefs.getStringSet(getString(R.string.settings_select_sections_key), getDefaultValues());
        String selectSections = values.toString()
                .replace(",", "")  //remove the commas
                .replace(" ", ",")  //add the commas
                .replace("[", "")  //remove the right bracket
                .replace("]", "")  //remove the left bracket
                .trim();           //remove trailing spaces from partially initialized arrays
        String uriDecode = null;

        Log.i("MainActivity", "values: " + selectSections);
        Uri baseUri = Uri.parse(API_URL);
        uriBuilder = baseUri.buildUpon();

        try {
            String decodedUrl = URLDecoder.decode(selectSections, "UTF-8");

            uriDecode = decodedUrl;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.i("MainActivity", "values after decoding: " + uriDecode);
        uriBuilder.appendQueryParameter("sections", uriDecode);
        URI_URL = uriBuilder.toString();
        Log.i("MainActivity", "query: " + URI_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, ArrayList<HashMap<String, String>> data) {
        if (data == null) {
            return;
        }
        newsList = data;

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
        if (!((NewsLoader) loader).serverResponse & networkInfo != null && networkInfo.isConnected()) {
            noDataTextView.setText(R.string.no_data_text);
            noDataImageView.setImageResource(R.drawable.ic_cloud_computing_2);

            hideNoInternet();

        } else {
            noDataTextView.setVisibility(View.INVISIBLE);
            noDataImageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {
        loader.reset();
    }

    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private Set<String> getDefaultValues() {
        final Set<String> defValues = new HashSet<String>();
        defValues.addAll(Arrays.asList(
                getResources().getStringArray(
                        R.array.settings_select_sections_defvalues)));
        return defValues;
    }

}



