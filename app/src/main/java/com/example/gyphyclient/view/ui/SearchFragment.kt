package com.example.gyphyclient.view.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import com.example.gyphyclient.R
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.view.adapter.TrendingAdapter
import com.example.gyphyclient.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.item_giphy.*
import javax.inject.Inject

class SearchFragment() : Fragment() {

    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var trendingAdapter: TrendingAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        search_button.setOnClickListener {
            viewModel.search(edit_text.text.toString())
            setUpRecyclerView()

            observeInProgress()
            observeIsError()
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
        return inflater.inflate(R.layout.fragment_search,container,false)
    }

    private fun setUpRecyclerView() {
        recycler_view_search.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = trendingAdapter
        }
    }

    private fun observeInProgress() {
        viewModel.repository.isInProgress.observe(this, Observer { isLoading ->
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
        viewModel.repository.isError.observe(this, Observer { isError ->
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

    private var gifForSave: Data? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun observeGiphyList() {
        viewModel.repository.data.observe(this, Observer { giphies ->
            giphies.let {
                if (it != null && it.isNotEmpty()) {
                    fetch_progress_search.visibility = View.VISIBLE
                    recycler_view_search.visibility = View.VISIBLE
                    trendingAdapter.setUpData(it,
                        { gif ->
                            //viewModel.gifShare(gif, requireContext())
                        },
                        { gif ->
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    //viewModel.gifSave(gif, requireContext())
                                } else {
                                    gifForSave = gif
                                    ActivityCompat.requestPermissions(
                                        requireActivity(),
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                        TrendingFragment.REQUEST_PERMISSION_WRITE_TO_EXT_STORAGE_CODE
                                    )
                                }
                            }
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