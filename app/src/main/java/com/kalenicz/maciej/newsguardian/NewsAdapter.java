package com.kalenicz.maciej.newsguardian;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

//       final News newsModel = newsList.get(position);

//         update text views
//        holder.headline.setText(newsModel.getHeadline());
//        holder.section.setText(newsModel.getSection());
//        holder.author.setText(newsModel.getAuthor());


    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    class NewsViewHolder extends RecyclerView.ViewHolder {

        final TextView headline;
        final TextView author;
        final TextView section;

        NewsViewHolder(View listItem) {
            super(listItem);
            headline = listItem.findViewById(R.id.headline);
            author = listItem.findViewById(R.id.author);
            section = listItem.findViewById(R.id.section);
        }
    }
}

