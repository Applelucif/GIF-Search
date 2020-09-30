package com.example.gyphyclient.view.ui

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gyphyclient.R
import com.example.gyphyclient.di.DaggerAppComponent
import com.example.gyphyclient.model.Data
import com.example.gyphyclient.view.adapter.TrendingAdapter
import com.example.gyphyclient.viewmodel.TrendingViewModel
import kotlinx.android.synthetic.main.fragment_top.*
import javax.inject.Inject


class TrendingFragment : Fragment() {
    @Inject
    lateinit var trendingAdapter: TrendingAdapter
    private val viewModel: TrendingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_top, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerAppComponent.create().inject(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpRecyclerView()

        observeLiveData()

    }

    private fun observeLiveData() {
        observeInProgress()
        observeIsError()
        observeGiphyList()
    }

    private var gifForSave: Data? = null
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun observeGiphyList() {
        viewModel.repository.data.observe(this, Observer { giphies ->
            giphies.let {
                if (it != null && it.isNotEmpty()) {
                    fetch_progress.visibility = View.VISIBLE
                    recycler_view.visibility = View.VISIBLE
                    trendingAdapter.setUpData(it,
                        { gif ->
                            viewModel.gifShare(gif, requireContext())
                        },
                        { gif ->
                            viewModel.addToFavorite(gif)
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

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_WRITE_TO_EXT_STORAGE_CODE -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED && gifForSave != null
                ) {
                    viewModel.gifSave(gifForSave!!, requireContext())

                } else {
                    Toast
                        .makeText(
                            requireContext(),
                            "Требуемые разрешения не предоставлены",
                            Toast.LENGTH_SHORT
                        )
                        .show()
                }
            }
        }
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
        trendingAdapter.setUpData(emptyList(), {}, {})
        fetch_progress.visibility = View.GONE
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

    private fun setUpRecyclerView() {

        recycler_view.apply {
            layoutManager = StaggeredGridLayoutManager(2, 1)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = trendingAdapter
            setActionInTheEnd {
                viewModel.getData(adapter?.itemCount ?: 0)
            }
        }
    }

    companion object {
        internal const val REQUEST_PERMISSION_WRITE_TO_EXT_STORAGE_CODE = 10
    }
}