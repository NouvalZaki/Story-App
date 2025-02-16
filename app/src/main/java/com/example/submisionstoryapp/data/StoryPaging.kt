package com.example.submisionstoryapp.data


import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.submisionstoryapp.api.ApiService
import com.example.submisionstoryapp.api.ListStoryItem

class StoryPaging (
    private val apiService: ApiService,
    private val token: String,
    private val location: Int? = null
) : PagingSource<Int, ListStoryItem>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1

        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        return try {
            val position = params.key ?: INITIAL_PAGE_INDEX

            val response = apiService.getAllStories(
                token = "Bearer $token",
                page = position,
                size = params.loadSize
            )

            if (response.isSuccessful) {
                val body = response.body()
                val stories = body?.listStory ?: emptyList()

                LoadResult.Page(
                    data = stories,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (stories.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("Error: ${response.message()}"))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}