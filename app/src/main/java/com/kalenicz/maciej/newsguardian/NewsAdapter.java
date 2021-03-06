package com.kalenicz.maciej.newsguardian;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private final ArrayList<HashMap<String, String>> newsList;

    NewsAdapter(ArrayList<HashMap<String, String>> list) {
        newsList = list;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        if (position >= getItemCount())
            return;

        HashMap<String, String> newsModel = newsList.get(position);

//         update text views
        holder.headline.setText(newsModel.get("headline"));
        holder.section.setText(newsModel.get("section"));
        holder.author.setText(newsModel.get("author"));
        holder.description.setText(newsModel.get("description"));
        holder.date.setText(newsModel.get("time"));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {

        final TextView headline;
        final TextView author;
        final TextView section;
        final TextView description;
        final TextView date;

        NewsViewHolder(View listItem) {
            super(listItem);
            headline = listItem.findViewById(R.id.headline);
            author = listItem.findViewById(R.id.author);
            section = listItem.findViewById(R.id.section);
            description = listItem.findViewById(R.id.description);
            date = listItem.findViewById(R.id.date);
        }
    }

}

