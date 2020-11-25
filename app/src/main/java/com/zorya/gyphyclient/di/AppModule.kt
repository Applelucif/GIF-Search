package com.zorya.gyphyclient.di

import com.zorya.gyphyclient.data.network.GiphyApi
import com.zorya.gyphyclient.data.network.GiphyApiService
import com.zorya.gyphyclient.model.Data
import com.zorya.gyphyclient.repository.TrendingRepository
import com.zorya.gyphyclient.view.adapter.FavoriteAdapter
import com.zorya.gyphyclient.view.adapter.TrendingAdapter
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {
    @Singleton
    @Provides
    fun provideApi(): GiphyApi = GiphyApiService.getClient()


    @Provides
    fun provideTrendingRepository() = TrendingRepository()

    @Provides
    fun provideListData() = ArrayList<Data>()

    @Provides
    fun provideTrendingAdapter(data: ArrayList<Data>): TrendingAdapter = TrendingAdapter(data)

    @Provides
    fun provideFavoriteAdapter(data: ArrayList<Data>): FavoriteAdapter = FavoriteAdapter(data)

}