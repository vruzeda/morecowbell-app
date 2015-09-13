package com.gaviota.morecowbell.controller.main;

import android.content.Context;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaviota.morecowbell.R;
import com.gaviota.morecowbell.model.Entry;
import com.gaviota.morecowbell.model.EntryDataSource;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by vruzeda on 9/12/15.
 */
public class MainRecyclerView extends android.support.v7.widget.RecyclerView {

    private static final int PAGINATION_LIMIT = 10;

    private EntryDataSource entryDataSource;

    public MainRecyclerView(Context context) {
        super(context);
        onCreate(context);
    }

    public MainRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        onCreate(context);
    }

    public MainRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        onCreate(context);
    }

    private void onCreate(Context context) {
        setHasFixedSize(true);
        setLayoutManager(new LinearLayoutManager(context));
        setItemAnimator(new DefaultItemAnimator());
        setAdapter(new MainRecyclerView.Adapter());

        entryDataSource = new Retrofit.Builder().baseUrl("http://cowbell.jptannus.com").addConverterFactory(GsonConverterFactory.create()).build().create(EntryDataSource.class);
        entryDataSource.getEntries(PAGINATION_LIMIT).enqueue(new Callback());
    }

    public void setOnEntryClickListener(OnEntryClickListener onEntryClickListener) {
        ((MainRecyclerView.Adapter) getAdapter()).onEntryClickListener = onEntryClickListener;
    }

    public void getMoreEntries() {
        MainRecyclerView.Adapter adapter = (MainRecyclerView.Adapter) getAdapter();
        if (adapter.entries != null && !adapter.entries.isEmpty()) {
            entryDataSource.getEntries(adapter.entries.get(adapter.entries.size() - 1).getId(), PAGINATION_LIMIT).enqueue(new MainRecyclerView.Callback());
        }
    }

    public interface OnEntryClickListener {

        void onEntryClick(Entry entry);

    }

    private static class Adapter extends android.support.v7.widget.RecyclerView.Adapter<MainRecyclerView.ViewHolder> {

        private List<Entry> entries;
        private OnEntryClickListener onEntryClickListener;

        @Override
        public int getItemCount() {
            if (entries != null) {
                return entries.size();
            } else {
                return 0;
            }
        }

        @Override
        public MainRecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_list_entry, parent, false);
            return new MainRecyclerView.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MainRecyclerView.ViewHolder holder, int position) {
            final Entry entry = entries.get(position);
            String title = entry.getTitle();
            final String thumbnailUrl = entry.getThumbnail(holder.context);

            holder.titleTextView.setText(title);
            Picasso.with(holder.context).load(thumbnailUrl).placeholder(R.drawable.video_placeholder).into(holder.thumbnailImageView);

            holder.entryView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Adapter.this.onEntryClickListener != null) {
                        Adapter.this.onEntryClickListener.onEntryClick(entry);
                    }
                }
            });
        }
    }

    private static class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder {

        public Context context;

        public View entryView;
        public ImageView thumbnailImageView;
        public TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();

            entryView = itemView.findViewById(R.id.activity_main_list_entry);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.activity_main_list_entry_thumbnail);
            titleTextView = (TextView) itemView.findViewById(R.id.activity_main_list_entry_title);
        }
    }

    private class Callback implements retrofit.Callback<List<Entry>> {

        @Override
        public void onResponse(Response<List<Entry>> response) {
            MainRecyclerView.Adapter adapter = (MainRecyclerView.Adapter) getAdapter();
            List<Entry> entries = response.body();

            if (adapter.entries == null) {
                adapter.entries = new ArrayList<>();
            }

            adapter.entries.addAll(entries);
            adapter.notifyDataSetChanged();

            if (entries.size() == PAGINATION_LIMIT) {
                getMoreEntries();
            }
        }

        @Override
        public void onFailure(Throwable t) {
            Log.d(MainRecyclerView.class.getSimpleName(), "Failure: " + t);
        }

    }

}
