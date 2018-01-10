package com.kalenicz.maciej.newsguardian;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.kalenicz.maciej.newsguardian.NewsLoader.API_URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>> {

    private static final String TAG = "MainActivity";
    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    TextView noDataTextView;
    ImageView noDataImageView;
    ArrayList<HashMap<String, String>> newsList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.list_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(new ArrayList<HashMap<String, String>>());
        recyclerView.setAdapter(adapter);
        TextView emptyStateTextView = findViewById(R.id.empty_view);
        ImageView emptyStateImageView = findViewById(R.id.empty_view_img);
        TextView emptyStateTextView2 = findViewById(R.id.empty_view2);
        noDataTextView = findViewById(R.id.no_data_news);
        noDataImageView = findViewById(R.id.no_data_news_img);


        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
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
                        Log.v(TAG, "onItemClick, position: " + position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Log.v(TAG, "onLongItemClick, position: " + position);
                    }

                })
        );
    }

    @Override
    public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int id, Bundle bundle) {
        return new NewsLoader(this, API_URL);
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
        if (((NewsLoader) loader).serverResponse == false) {
            noDataTextView.setText(R.string.no_data_text);
            noDataImageView.setImageResource(R.drawable.ic_cloud_computing_2);
        } else {
            noDataTextView.setVisibility(View.GONE);
            noDataImageView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {
        loader.reset();
    }
}



