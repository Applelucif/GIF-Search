package com.example.gyphyclient.view.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gyphyclient.R
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.view.adapter.FavoriteAdapter
import com.example.gyphyclient.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.favorite_fragment.*
import javax.inject.Inject

class FavoriteFragment: Fragment() {

    private val viewModel: FavoriteViewModel by viewModels()

    @Inject
    lateinit var favoriteAdapter: FavoriteAdapter

    fun backToFavoriteFragment() {
        setUpRecyclerView()
        observeLiveData()
    }

    private fun observeLiveData() {
        observeInProgress()
        observeIsError()
        observeGiphyList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent.create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_fragment,container,false)
    }

    private fun setUpRecyclerView() {
        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(2, 1)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = favoriteAdapter
        }
    }

    private fun observeInProgress() {
        viewModel.repository.isInProgress.observe(this, Observer { isLoading ->
            isLoading.let {
                if (it) {
                    empty_text.visibility = View.GONE
                    recycler_view.visibility = View.GONE
                    fetch_progress.visibility = View.VISIBLE
                } else {
                    fetch_progress.visibility = View.GONE
                }
            }
        })
    }

    private fun observeIsError() {
        viewModel.repository.isError.observe(this, Observer { isError ->
            isError.let {
                if (it) {
                    disableViewsOnError()
                } else {
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun disableViewsOnError() {
        fetch_progress.visibility = View.VISIBLE
        empty_text.visibility = View.VISIBLE
        recycler_view.visibility = View.GONE
        favoriteAdapter.setUpData(emptyList(), {})
        fetch_progress.visibility = View.GONE
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun observeGiphyList() {
        viewModel.repository.data.observe(this, Observer { giphies ->
            giphies.let {
                if (it != null && it.isNotEmpty()) {
                    fetch_progress.visibility = View.VISIBLE
                    recycler_view.visibility = View.VISIBLE
                    favoriteAdapter.setUpData(it
                    ) { gif ->
                        viewModel.gifShare(gif, requireContext())
                    }
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.GONE
                } else {
                    disableViewsOnError()
                }
            }
        })
    }
}