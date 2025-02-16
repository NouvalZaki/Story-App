package com.example.submisionstoryapp.ui.helper

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submisionstoryapp.data.StoryRepo
import com.example.submisionstoryapp.data.UserRepo
import com.example.submisionstoryapp.ui.daftar.SignUpViewModel
import com.example.submisionstoryapp.ui.login.ViewModelLogin
import com.example.submisionstoryapp.ui.main.MainViewModel
import com.example.submisionstoryapp.ui.maps.MapsViewModel

class ViewModelFactory (private val userRepository: UserRepo,
                        private val storyRepository: StoryRepo
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository,
                    storyRepository
                )
                        as T
            }

            modelClass.isAssignableFrom(ViewModelLogin::class.java) -> {
                ViewModelLogin(userRepository) as T
            }

            modelClass.isAssignableFrom(SignUpViewModel::class.java) -> {
                SignUpViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(storyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context),
                        Injection.provideStoryRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}