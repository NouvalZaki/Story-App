package com.example.submisionstoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.submisionstoryapp.api.AddResponse
import com.example.submisionstoryapp.api.AllResponse
import com.example.submisionstoryapp.api.ApiService
import com.example.submisionstoryapp.api.ListStoryItem
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepo private constructor(private val apiService: ApiService) {
    fun getPagedStories(token: String): Flow<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPaging(apiService, token) }
        ).flow
    }


    fun uploadStory(
        token: String,
        imageFile: File,
        description: String
    ): LiveData<Result<AddResponse>> = liveData {
        emit(Result.Load)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        try {
            val successResponse = apiService.uploadStory("Bearer $token", multipartBody, requestBody)
            emit(Result.Success(successResponse))
        } catch (e: HttpException) {
            Log.e("uploadStory", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, AddResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (e: Exception) {
                Log.e("uploadStory", "Error parsing error response: ${e.message}")
                emit(Result.Error("Error: ${e.message}"))
            }
        } catch (e: Exception) {
            Log.e("uploadStory", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    suspend fun getStoriesWithLocation(token: String, location: Int = 1): Result<AllResponse> {
        return try {
            val response = apiService.getStoriesWithLocation(token, location)
            if (response.isSuccessful && response.body() != null) {
                Result.Success(response.body()!!)
            } else {
                Result.Error(response.message() ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "An error occurred")
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepo? = null

        fun getInstance(apiService: ApiService): StoryRepo =
            instance ?: synchronized(this) {
                instance ?: StoryRepo(apiService).also { instance = it }
            }
    }
}