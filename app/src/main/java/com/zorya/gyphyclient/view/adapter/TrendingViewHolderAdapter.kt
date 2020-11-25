package com.example.gyphyclient.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gyphyclient.R
import com.example.gyphyclient.databinding.ItemGiphyBinding
import com.example.gyphyclient.internal.LIMIT
import com.example.gyphyclient.model.Data

class TrendingViewHolder(
    private val itemGiphyBinding: ItemGiphyBinding
) : RecyclerView.ViewHolder(itemGiphyBinding.root) {
    private val shareBtn: ImageView = itemGiphyBinding.root.findViewById(R.id.share_button)
    private val saveBtn: ImageView = itemGiphyBinding.root.findViewById(R.id.favorite_button)

    fun bind(gif: Data, shareGif: (Data) -> Unit, saveGif: (Data) -> Unit) {
        itemGiphyBinding.data = gif
        shareBtn.setOnClickListener {
            shareGif(gif)
        }

        saveBtn.setOnClickListener {
            saveGif(gif)
        }
    }
}

class TrendingAdapter(val data: ArrayList<Data>) : RecyclerView.Adapter<TrendingViewHolder>() {

    private var shareGif: (Data) -> Unit = {}
    private var favoriteGif: (Data) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrendingViewHolder {
        val itemGiphyBinding: ItemGiphyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_giphy,
            parent,
            false
        )

        return TrendingViewHolder(itemGiphyBinding)
    }

    fun setUpData(giphies: List<Data>, shareGif: (Data) -> Unit, saveGif: (Data) -> Unit) {
        data.clear()
        data.addAll(giphies)
        notifyItemRangeChanged(data.size - LIMIT.toInt(), LIMIT.toInt())
        this.shareGif = shareGif
        this.favoriteGif = saveGif
    }

    override fun onBindViewHolder(holder: TrendingViewHolder, position: Int) {
        val gif = data[position]
        holder.bind(gif, shareGif, favoriteGif)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}