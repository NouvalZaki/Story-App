package com.example.submisionstoryapp.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPref private constructor(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserData) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.userId
            preferences[EMAIL_KEY] = user.email
            preferences[TOKEN_KEY] = user.token
            preferences[IS_LOGIN_KEY] = user.isLogin
        }
    }

    fun getSession(): Flow<UserData> {
        return dataStore.data.map { pref ->
            UserData(
                userId = pref[ID_KEY] ?: "",
                email = pref[EMAIL_KEY] ?: "",
                token = pref[TOKEN_KEY] ?: "",
                isLogin = pref[TOKEN_KEY]?.isNotEmpty() ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    suspend fun removeEmail() {
        dataStore.edit { preferences ->
            preferences.remove(EMAIL_KEY)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPref? = null


        private val EMAIL_KEY = stringPreferencesKey("email")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
        private val ID_KEY = stringPreferencesKey("userId")

        fun getInstance(dataStore: DataStore<Preferences>): UserPref {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPref(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}