package com.example.submisionstoryapp.data

sealed class Result <out R> private constructor() {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val massage: String) : Result<Nothing>()
    object Load : Result<Nothing>()
}