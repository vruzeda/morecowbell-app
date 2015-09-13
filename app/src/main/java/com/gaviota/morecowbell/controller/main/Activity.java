package com.gaviota.morecowbell.controller.main;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.gaviota.morecowbell.R;
import com.gaviota.morecowbell.model.Entry;
import com.gaviota.morecowbell.model.EntryDataSource;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainRecyclerView entryList = (MainRecyclerView) findViewById(R.id.activity_main_list);
        entryList.setOnEntryClickListener(new MainRecyclerView.OnEntryClickListener() {
            @Override
            public void onEntryClick(Entry entry) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getSource()));
                startActivity(intent);
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.activity_main_list_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddEntryDialogFragment dialogFragment = new AddEntryDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), AddEntryDialogFragment.class.getSimpleName());
            }
        });
    }

    public class AddEntryDialogFragment extends AppCompatDialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            View dialogView = LayoutInflater.from(Activity.this).inflate(R.layout.activity_main_list_add, null);
            final EditText titleEditText = (EditText) dialogView.findViewById(R.id.activity_main_list_add_title);
            final EditText sourceEditText = (EditText) dialogView.findViewById(R.id.activity_main_list_add_source);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.activity_main_list_add_title);
            builder.setView(dialogView);
            builder.setPositiveButton(R.string.activity_main_list_add_positive_button, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String title = titleEditText.getText().toString();
                    String source = sourceEditText.getText().toString();

                    if (title.isEmpty()) {
                        return;
                    }

                    if (source.isEmpty()) {
                        return;
                    }

                    EntryDataSource entryDataSource = new Retrofit.Builder().baseUrl("http://cowbell.jptannus.com").addConverterFactory(GsonConverterFactory.create()).build().create(EntryDataSource.class);
                    entryDataSource.newEntry(new Entry(title, source)).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Response<Void> response) {
                            MainRecyclerView entryList = (MainRecyclerView) Activity.this.findViewById(R.id.activity_main_list);
                            entryList.getMoreEntries();
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            Log.d("MainActivity", "Failure: " + t);
                        }
                    });
                }
            });
            builder.setNegativeButton(R.string.activity_main_list_add_negative_button, new Dialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            return builder.create();
        }
    }

}
