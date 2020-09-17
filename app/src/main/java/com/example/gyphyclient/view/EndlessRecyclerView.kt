package com.example.gyphyclient.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gyphyclient.repository.TrendingRepository

class EndlessRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    RecyclerView(context, attrs, defStyleAttr) {

    init {
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.layoutManager != null) {
                    totalItemCount = recyclerView.layoutManager!!.getItemCount()
                    lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                        Toast.makeText(context, "The end, load next data", Toast.LENGTH_SHORT)
                            .show()
                        somethingActionWhenNearEnd() //когда наступит конец, здесь выполнится какое-то действие
                    }
                }
            }
        })
    }

    private var lastVisibleItemPosition: Int = 0
    private var totalItemCount: Int = 0
    var isLoading = false

    private var somethingActionWhenNearEnd: () -> Unit = {} //какое-то действие

    //метод который инициализирует чем-то какое-то действие
    fun setActionInTheEnd(action: () -> Unit) {
        somethingActionWhenNearEnd = action
    }
}