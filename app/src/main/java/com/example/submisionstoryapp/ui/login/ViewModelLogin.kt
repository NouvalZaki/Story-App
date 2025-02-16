package com.example.submisionstoryapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submisionstoryapp.data.UserRepo
import com.example.submisionstoryapp.data.preference.UserData
import kotlinx.coroutines.launch

class ViewModelLogin (private val repository: UserRepo) : ViewModel() {
    fun saveSession(user: UserData) {
        viewModelScope.launch { repository.saveSession(user) }
    }
    fun login(email: String, password: String) = repository.login(email, password)
}