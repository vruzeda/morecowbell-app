package com.gaviota.morecowbell.model;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

public interface EntryDataSource {

    @GET("/entries")
    Call<List<Entry>> getEntries(@Query("limit") int limit);

    @GET("/entries")
    Call<List<Entry>> getEntries(@Query("after") String after, @Query("limit") int limit);

    @POST("/entries")
    Call<Void> newEntry(@Body Entry entry);

}