package com.example.gyphyclient.di

import com.example.gyphyclient.repository.TrendingRepository
import com.example.gyphyclient.view.ui.SearchFragment
import com.example.gyphyclient.view.ui.TrendingFragment
import com.example.gyphyclient.viewmodel.SearchViewModel
import com.example.gyphyclient.viewmodel.TrendingViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(trendingRepository: TrendingRepository)
    fun inject(viewModel: TrendingViewModel)
    fun inject(topFragment: TrendingFragment)
    fun inject(searchViewModel: SearchViewModel)
    fun inject(srachFragment: SearchFragment)
}