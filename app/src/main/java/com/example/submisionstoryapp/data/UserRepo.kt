package com.example.submisionstoryapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.submisionstoryapp.api.AddResponse
import com.example.submisionstoryapp.api.ApiService
import com.example.submisionstoryapp.api.LoginResponse
import com.example.submisionstoryapp.api.RegistResponse
import com.example.submisionstoryapp.data.preference.UserData
import com.example.submisionstoryapp.data.preference.UserPref
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File


class UserRepo private constructor(
    private val userPreference: UserPref,
    private val apiService: ApiService
) {


    fun signup(name: String, email: String, password: String): LiveData<Result<RegistResponse>> = liveData {
        emit(Result.Load)
        try {
            val response = apiService.register(name, email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("postRegister", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, RegistResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (parseException: Exception) {
                Log.e("postRegister", "Error parsing response: ${parseException.message}")
                emit(Result.Error("Error parsing HTTP exception response"))
            }
        } catch (e: Exception) {
            Log.e("postRegister", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
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


    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Load)
        try {
            val response = apiService.login(email, password)
            emit(Result.Success(response))
        } catch (e: HttpException) {
            Log.e("postLogin", "HTTP Exception: ${e.message}")
            try {
                val errorResponse = e.response()?.errorBody()?.string()
                val gson = Gson()
                val parsedError = gson.fromJson(errorResponse, LoginResponse::class.java)
                emit(Result.Success(parsedError))
            } catch (parseException: Exception) {
                Log.e("postLogin", "Error parsing response: ${parseException.message}")
                emit(Result.Error("Error parsing HTTP exception response"))
            }
        } catch (e: Exception) {
            Log.e("postLogin", "General Exception: ${e.message}")
            emit(Result.Error(e.message.toString()))
        }
    }

    // Save the user session
    suspend fun saveSession(user: UserData) {
        userPreference.saveSession(user)
    }

    // Get the current session
    fun getSession(): Flow<UserData> {
        return userPreference.getSession()
    }

    // Logout function: Remove the session data and email
    suspend fun logout() {
        // Remove user session and email from preferences
        userPreference.logout()
        userPreference.removeEmail()

        // Optionally, you can also clear other relevant user data like tokens if needed
        Log.d("UserRepo", "User logged out successfully")
    }

    // Remove user's email from preferences
    suspend fun removeEmail() {
        userPreference.removeEmail()
    }

    companion object {
        @Volatile
        private var instance: UserRepo? = null

        // Singleton pattern to get the instance of UserRepo
        fun getInstance(
            userPreference: UserPref,
            apiService: ApiService
        ): UserRepo =
            instance ?: synchronized(this) {
                instance ?: UserRepo(userPreference, apiService)
            }.also { instance = it }
    }
}
