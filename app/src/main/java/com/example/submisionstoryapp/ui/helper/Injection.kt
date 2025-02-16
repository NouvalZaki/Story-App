package com.example.submisionstoryapp.ui.helper


import android.content.Context
import com.example.submisionstoryapp.api.ApiConfig
import com.example.submisionstoryapp.data.StoryRepo
import com.example.submisionstoryapp.data.UserRepo
import com.example.submisionstoryapp.data.preference.UserPref
import com.example.submisionstoryapp.data.preference.dataStore

object Injection {
    fun provideStoryRepository(context: Context): StoryRepo {
        val pref = UserPref.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return StoryRepo.getInstance(apiService)
    }

    fun provideRepository(context: Context): UserRepo {
        val pref = UserPref.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepo.getInstance(pref, apiService)
    }
}