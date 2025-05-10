package com.example.newsappliaction.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsappliaction.R
import com.example.newsappliaction.data.remote.news.ArticlesItem
import com.example.newsappliaction.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.text.category

class NewsAdapter(
    private val onItemClick: (ArticlesItem) -> Unit,
    private val onFavoriteClick: (ArticlesItem, Boolean) -> Unit
) : ListAdapter<ArticlesItem, NewsAdapter.NewsViewHolder>(NewsDiffCallback()) {

    private val favoriteStatusMap = mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateFavoriteStatus(url: String, isFavorite: Boolean) {
        if (favoriteStatusMap[url] != isFavorite) {
            favoriteStatusMap[url] = isFavorite
            // Find the item and update only that specific item
            val position = currentList.indexOfFirst { it.url == url }
            if (position >= 0) {
                notifyItemChanged(position)
            }
        }
    }

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        // In NewsAdapter.kt, update the bind method
        fun bind(article: ArticlesItem) {
            binding.apply {
                tvTitle.text = article.title
                tvDescription.text = article.description
                tvSource.text = article.source?.name

                // Format date to be more user-friendly
                article.publishedAt?.let {
                    try {
                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        val date = inputFormat.parse(it)
                        val outputFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
                        tvPublishedAt.text = date?.let { d -> outputFormat.format(d) }
                    } catch (e: Exception) {
                        tvPublishedAt.text = it
                    }
                }

                // Show category if available
                tvCategory.visibility = View.VISIBLE
                tvCategory.text = (article.category ?: "general").uppercase()

                Glide.with(ivNewsImage.context)
                    .load(article.urlToImage)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.error_image)
                    .into(ivNewsImage)
            }

            val isFavorite = favoriteStatusMap[article.url] ?: false
            binding.btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            binding.btnFavorite.setOnClickListener {
                val newStatus = !(favoriteStatusMap[article.url] ?: false)
                onFavoriteClick(article, newStatus)
                favoriteStatusMap[article.url.toString()] = newStatus
                binding.btnFavorite.setImageResource(
                    if (newStatus) R.drawable.ic_favorite else R.drawable.ic_favorite_border
                )
            }
        }
    }

    class NewsDiffCallback : DiffUtil.ItemCallback<ArticlesItem>() {
        override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
            return oldItem == newItem
        }
    }
}