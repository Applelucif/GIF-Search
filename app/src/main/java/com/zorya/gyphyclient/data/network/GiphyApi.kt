package com.zorya.gyphyclient.data.network

import com.zorya.gyphyclient.model.Result
import io.reactivex.Flowable
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface GiphyApi {
    @GET("v1/gifs/trending")
    fun getTrending(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: String,
        @Query("rating") rating: String,
        @Query("offset") offset: String
    ): Flowable<Result>

    @GET("v1/gifs/search")
    fun getSeach(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: String,
        @Query("rating") rating: String,
        @Query("q") searchTerm: String
    ): Single<Result>
}