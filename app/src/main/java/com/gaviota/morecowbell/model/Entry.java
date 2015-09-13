package com.gaviota.morecowbell.model;

import android.content.Context;

import com.gaviota.morecowbell.R;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vruzeda on 9/12/15.
 */
public class Entry implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("_id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("source")
    private String source;

    public Entry(String title, String source) {
        this.title = title;
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getThumbnail(Context context) {
        return context.getString(R.string.entry_thumbnail_youtube_url, getVideoId());
    }

    private String getVideoId() {
        Pattern pattern = Pattern.compile("^.*(?:(?:youtu\\.be\\/)|(?:v\\/)|(?:\\/u\\/\\w\\/)|(?:embed\\/)|(?:watch\\?))\\??v?=?([^#\\&\\?]*).*");
        Matcher matcher = pattern.matcher(source);
        if (matcher.matches()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
