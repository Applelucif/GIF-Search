package com.example.gyphyclient.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gyphyclient.R
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.view.adapter.TrendingAdapter
import com.example.gyphyclient.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_top.*
import javax.inject.Inject

class TrendingFragment : Fragment() {
    @Inject
    lateinit var trendingAdapter: TrendingAdapter
    private val viewModel: TrendingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent.create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpRecyclerView()
        observeInProgress()
        observeIsError()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            observeGiphyList()
        }
    }

    private fun setUpRecyclerView() {
        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(SPAN_COUNT, ORIENTATION)
            adapter = trendingAdapter
            setActionInTheEnd {
                viewModel.getData(adapter?.itemCount ?: 0)
            }
        }
    }

    private fun observeInProgress() {
        viewModel._isInProgress.observe(viewLifecycleOwner, { isLoading ->
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
        viewModel._isError.observe(viewLifecycleOwner, { isError ->
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
        trendingAdapter.setUpData(emptyList(), {}, {})
        fetch_progress.visibility = View.GONE
    }

    private fun observeGiphyList() {
        viewModel.data.observe(viewLifecycleOwner, { giphies ->
            giphies?.let {
                if (it.isNotEmpty()) {
                    fetch_progress.visibility = View.VISIBLE
                    recycler_view.visibility = View.VISIBLE
                    recycler_view.isLoading = false
                    trendingAdapter.setUpData(it,
                        { gif ->
                            viewModel.repository.gifShare(gif, requireContext())
                        },
                        { gif ->
                            viewModel.addToFavorite(gif)
                            viewModel.gifSave(gif, requireContext())
                        }
                    )
                    empty_text.visibility = View.GONE
                    fetch_progress.visibility = View.GONE
                } else {
                    disableViewsOnError()
                }
            }
        })
    }

    companion object {
        private const val SPAN_COUNT = 2
        private const val ORIENTATION = 1
    }
}