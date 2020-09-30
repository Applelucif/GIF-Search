package com.example.gyphyclient.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gyphyclient.R
import com.example.gyphyclient.databinding.ItemFavoriteGiphyBinding
import com.example.gyphyclient.model.Data

class FavoriteViewHolder(
    private val itemGiphyBinding: ItemFavoriteGiphyBinding
) : RecyclerView.ViewHolder(itemGiphyBinding.root) {

    private val shareBtn: ImageView = itemGiphyBinding.root.findViewById(R.id.share_button)

    fun bind(gif: Data, shareGif: (Data) -> Unit) {
        itemGiphyBinding.data = gif
        shareBtn.setOnClickListener {
            shareGif(gif)
        }
    }
}

class FavoriteAdapter(val data: ArrayList<Data>) : RecyclerView.Adapter<FavoriteViewHolder>() {

    private var shareGif: (Data) -> Unit = {}

    fun setUpData(giphies: List<Data>, shareGif: (Data) -> Unit) {
        data.clear()
        data.addAll(giphies)
        notifyDataSetChanged()
        this.shareGif = shareGif
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemGiphyBinding: ItemFavoriteGiphyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_favorite_giphy,
            parent,
            false
        )

        return FavoriteViewHolder(itemGiphyBinding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val gif = data[position]
        holder.bind(gif, shareGif)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}