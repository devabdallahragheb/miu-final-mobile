package com.example.finalexam.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {
    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
        val LAST_JOKE = stringPreferencesKey("last_joke")
    }
    
    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }
    
    val username: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[USERNAME] ?: ""
    }
    
    val lastJoke: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LAST_JOKE] ?: ""
    }
    
    suspend fun setLoggedIn(isLoggedIn: Boolean, username: String = "") {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USERNAME] = username
        }
    }
    
    suspend fun saveJoke(joke: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_JOKE] = joke
        }
    }
}
