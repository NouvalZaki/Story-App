package com.example.submisionstoryapp.ui.helper

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submisionstoryapp.api.ListStoryItem
import com.example.submisionstoryapp.databinding.CardStoryBinding
import com.example.submisionstoryapp.ui.story.DetailActivity
import com.example.submisionstoryapp.ui.story.DetailActivity.Companion.STORY

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    private var onItemClickCallback: OnItemClickCallback? = null

    inner class StoryViewHolder(private val binding: CardStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.storyName.text = story.name
            binding.description.text = story.description
            Glide.with(binding.root.context)
                .load(story.photoUrl)
                .into(binding.image)

            binding.cardStory.setOnClickListener {

                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(STORY, story)
                itemView.context.startActivity(intent)


                onItemClickCallback?.onItemClicked(story.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = CardStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        story?.let { holder.bind(it) }
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(storyId: String)
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
