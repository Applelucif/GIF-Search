package com.example.gyphyclient.di

import com.example.gyphyclient.data.network.GiphyApi
import com.example.gyphyclient.data.network.GiphyApiService
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.repository.TrendingRepository
import com.example.gyphyclient.view.adapter.TrendingAdapter
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
}