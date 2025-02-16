package com.example.submisionstoryapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submisionstoryapp.api.AllResponse
import com.example.submisionstoryapp.data.StoryRepo
import kotlinx.coroutines.launch
import com.example.submisionstoryapp.data.Result

class MapsViewModel (private val storyRepository: StoryRepo) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<AllResponse>()
    val storiesWithLocation: LiveData<AllResponse> get() = _storiesWithLocation

    fun getStoriesWithLocation(token: String) {
        viewModelScope.launch {
            when (val result = storyRepository.getStoriesWithLocation(token)) {
                is Result.Success -> {
                    _storiesWithLocation.postValue(result.data) // Success case
                }
                is Result.Error -> {
                    // Handle error: Add an empty list or error state
                    _storiesWithLocation.postValue(AllResponse(emptyList(), true, result.massage))
                }
                Result.Load -> {

                }
            }
        }
    }
}