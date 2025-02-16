package com.example.submisionstoryapp.ui.main

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submisionstoryapp.api.ListStoryItem
import com.example.submisionstoryapp.data.StoryRepo
import com.example.submisionstoryapp.data.UserRepo
import com.example.submisionstoryapp.data.preference.UserData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import com.example.submisionstoryapp.data.Result


class MainViewModel (private val repository: UserRepo, private val storyRepository: StoryRepo) : ViewModel() {
    private var _currentImageUri = MutableLiveData<Uri?>()
    private val _storiesWithLocation = MutableLiveData<PagingData<ListStoryItem>>()



    private val _storyLoadState = MutableLiveData<Result<PagingData<ListStoryItem>>>()


        fun getSession(): LiveData<UserData> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun uploadStory(token: String, file: File, description: String) = repository.uploadStory(token, file, description)

    fun setCurrentImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }


    val stories: LiveData<PagingData<ListStoryItem>> = liveData {
        val user = repository.getSession().first()
        if (user.isLogin) {
            val token = user.token
            emitSource(
                storyRepository.getPagedStories(token)
                    .cachedIn(viewModelScope)
                    .asLiveData()
            )
        }
    }

}

