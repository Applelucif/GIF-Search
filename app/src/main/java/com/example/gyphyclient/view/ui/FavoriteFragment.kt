package com.example.gyphyclient.view.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gyphyclient.R
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.view.adapter.FavoriteAdapter
import com.example.gyphyclient.viewmodel.FavoriteViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.favorite_fragment.*
import javax.inject.Inject

class FavoriteFragment : Fragment() {

    private val viewModel: FavoriteViewModel by viewModels()

    @Inject
    lateinit var favoriteAdapter: FavoriteAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setUpRecyclerView()
        observeInProgress()
        observeIsError()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            observeGiphyList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent.create().inject(this)
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_CLASS, TAG)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_fragment, container, false)
    }

    private fun setUpRecyclerView() {
        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(SPAN_COUNT, ORIENTATION)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = favoriteAdapter
        }
    }

    private fun observeInProgress() {
        viewModel._isInProgress.observe(this, Observer { isLoading ->
            isLoading.let {
                if (it) {
                    empty_text.visibility = View.GONE
                    recycler_view.visibility = View.GONE
                    fetch_progress.visibility = View.VISIBLE
                } else {
                    fetch_progress.visibility = View.GONE
                    empty_text.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun observeIsError() {
        viewModel._isError.observe(this, Observer { isError ->
            empty_text.visibility = View.GONE
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

    private fun observeGiphyList() {
        viewModel.data.observe(this, Observer { giphies ->
            giphies.let {
                if (it!= null && it.isNotEmpty()) {
                    fetch_progress.visibility = View.VISIBLE
                    recycler_view.visibility = View.VISIBLE
                    favoriteAdapter.setUpData(
                        it
                    ) { gif ->
                        viewModel.repository.gifShare(gif, requireContext())
                    }
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
        const val TAG = "FavoriteFragment"
    }
}