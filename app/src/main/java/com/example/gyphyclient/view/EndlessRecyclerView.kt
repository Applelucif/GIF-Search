package com.example.gyphyclient.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.gyphyclient.R

class EndlessRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RecyclerView(context, attrs, defStyleAttr) {
    var isLoading = false

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
                var maxSize = 0
                for (i in lastVisibleItemPositions.indices) {
                    if (i == 0) {
                        maxSize = lastVisibleItemPositions[i]
                    } else if (lastVisibleItemPositions[i] > maxSize) {
                        maxSize = lastVisibleItemPositions[i]
                    }
                }
                return maxSize
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.layoutManager != null) {
                    totalItemCount = recyclerView.layoutManager!!.getItemCount()

                    var lastVisibleItemPositions =
                        (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(
                            null
                        )
                    var lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                    if (!isLoading && lastVisibleItemPosition == totalItemCount - lastMinusThisPosition) {
                        Toast.makeText(context, R.string.end_data, Toast.LENGTH_SHORT)
                            .show()
                        isLoading = true
                        somethingActionWhenNearEnd() //когда наступит конец, здесь выполнится какое-то действие
                    }
                    if (lastVisibleItemPosition == totalItemCount) {
                        Toast.makeText(context, R.string.end_data, Toast.LENGTH_SHORT)
                            .show()
                        isLoading = true
                        somethingActionWhenNearEnd()
                    }
                }
            }
        })
    }

    private var totalItemCount: Int = 0

    private var somethingActionWhenNearEnd: () -> Unit = {} //какое-то действие

    //метод который инициализирует чем-то какое-то действие
    fun setActionInTheEnd(action: () -> Unit) {
        somethingActionWhenNearEnd = action
    }

    companion object {
        private const val lastMinusThisPosition = 6
    }
}