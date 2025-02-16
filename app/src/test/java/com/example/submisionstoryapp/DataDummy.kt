package com.example.submisionstoryapp

import com.example.submisionstoryapp.api.AllResponse
import com.example.submisionstoryapp.api.ListStoryItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataDummy {

    fun generateDummyStoryResponse(): AllResponse {
        val items = mutableListOf<ListStoryItem>()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        for (i in 0..100) {
            val story = ListStoryItem(
                photoUrl = "https://dummyimage.com/600x400/000/fff&text=Story+$i",
                createdAt = currentDate,
                name = "author $i",
                description = "description $i",
                lon = 123.0 + i,
                id = "story_$i",
                lat = 456.0 + i
            )
            items.add(story)
        }

        return AllResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = items
        )
    }
}