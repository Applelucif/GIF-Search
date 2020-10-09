package com.example.gyphyclient.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.gyphyclient.R
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.view.adapter.TrendingAdapter
import com.example.gyphyclient.viewmodel.SearchViewModel
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_search.*
import javax.inject.Inject


class SearchFragment() : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private val compositeDisposable by lazy { CompositeDisposable() }

    @Inject
    lateinit var trendingAdapter: TrendingAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(text: String): Boolean {
                viewModel.search(text)
                return true
            }
        })

        //TODO вынести в xml
        fetch_progress_search.visibility = View.GONE
        setUpRecyclerView()
        observeInProgress()
        observeIsError()
        //TODO разобраться почему требуется выше 5 android
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            observeGiphyList()
        }
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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onStart() {
        super.onStart()
        compositeDisposable.add(
            viewModel.getGifFlow()
                .subscribe() { listEntity ->
                    trendingAdapter.setUpData(listEntity, {}, {})
                }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun setUpRecyclerView() {
        recycler_view_search.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = trendingAdapter
        }
    }

    private fun observeInProgress() {
        viewModel.repository.isInProgress.observe(viewLifecycleOwner, { isLoading ->
            isLoading.let {
                if (it) {
                    empty_text_search.visibility = View.GONE
                    recycler_view_search.visibility = View.GONE
                    fetch_progress_search.visibility = View.VISIBLE
                } else {
                    fetch_progress_search.visibility = View.GONE
                }
            }
        })
    }

    private fun observeIsError() {
        viewModel.repository.isError.observe(viewLifecycleOwner, { isError ->
            isError.let {
                if (it) {
                    disableViewsOnError()
                } else {
                    empty_text_search.visibility = View.GONE
                    fetch_progress_search.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun disableViewsOnError() {
        fetch_progress_search.visibility = View.VISIBLE
        empty_text_search.visibility = View.VISIBLE
        recycler_view_search.visibility = View.GONE
        trendingAdapter.setUpData(emptyList(), {}, {})
        fetch_progress_search.visibility = View.GONE
    }

    private fun observeGiphyList() {
        viewModel.repository.data.observe(viewLifecycleOwner, { giphies ->
            giphies?.let {
                if (it.isNotEmpty()) {
                    fetch_progress_search.visibility = View.VISIBLE
                    recycler_view_search.visibility = View.VISIBLE
                    trendingAdapter.setUpData(it,
                        { gif ->
                            viewModel.gifShare(gif, requireContext())
                        },
                        { gif ->
                            viewModel.addToFavorite(gif)
                        }
                    )
                    empty_text_search.visibility = View.GONE
                    fetch_progress_search.visibility = View.GONE
                } else {
                    disableViewsOnError()
                }
            }
        })
    }
}