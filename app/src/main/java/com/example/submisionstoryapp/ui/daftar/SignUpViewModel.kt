package com.example.submisionstoryapp.ui.daftar

import androidx.lifecycle.ViewModel
import com.example.submisionstoryapp.data.UserRepo

class SignUpViewModel(private val repository: UserRepo) : ViewModel() {
    fun signup(name: String, email: String, password: String) = repository.signup(name, email, password)
}