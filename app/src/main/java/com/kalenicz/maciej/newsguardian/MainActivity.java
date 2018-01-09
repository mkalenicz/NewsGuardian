package com.kalenicz.maciej.newsguardian;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import static com.kalenicz.maciej.newsguardian.NewsLoader.API_URL;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<HashMap<String, String>>> {

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<HashMap<String, String>> newsList;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(1, null, this);
    }

    @Override
    public Loader<ArrayList<HashMap<String, String>>> onCreateLoader(int id, Bundle bundle) {
        return new NewsLoader(this, API_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<HashMap<String, String>>> loader, ArrayList<HashMap<String, String>> data) {
        newsList = data;
        recyclerView = findViewById(R.id.list_recycler_view);
        layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NewsAdapter(newsList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<HashMap<String, String>>> loader) {
        loader.reset();
    }
}



