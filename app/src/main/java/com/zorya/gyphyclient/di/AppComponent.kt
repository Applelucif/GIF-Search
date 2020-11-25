package com.zorya.gyphyclient.di

import com.zorya.gyphyclient.repository.TrendingRepository
import com.zorya.gyphyclient.view.ui.FavoriteFragment
import com.zorya.gyphyclient.view.ui.SearchFragment
import com.zorya.gyphyclient.view.ui.TrendingFragment
import com.zorya.gyphyclient.viewmodel.FavoriteViewModel
import com.zorya.gyphyclient.viewmodel.SearchViewModel
import com.zorya.gyphyclient.viewmodel.TrendingViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(trendingRepository: TrendingRepository)
    fun inject(viewModel: TrendingViewModel)
    fun inject(topFragment: TrendingFragment)
    fun inject(searchViewModel: SearchViewModel)
    fun inject(searchFragment: SearchFragment)
    fun inject(favoriteViewModel: FavoriteViewModel)
    fun inject(favoriteFragment: FavoriteFragment)
}