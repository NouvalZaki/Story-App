package com.example.submisionstoryapp.ui.story

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.submisionstoryapp.R
import com.example.submisionstoryapp.api.ListStoryItem
import com.example.submisionstoryapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = getString(R.string.description)

        val story = intent.getParcelableExtra<ListStoryItem>(STORY) as ListStoryItem

        Glide
            .with(this)
            .load(story.photoUrl)
            .placeholder(R.drawable.baseline_image_24)
            .into(binding.pict)
        binding.tvTitle.text = story.name
        binding.description.text = story.description
    }


    companion object {
        const val STORY = "story"
    }
}