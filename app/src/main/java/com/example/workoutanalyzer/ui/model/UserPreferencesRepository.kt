package com.example.workoutanalyzer.ui.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private val APP_DATA_KEY = stringPreferencesKey("app_data")

    val appData: Flow<AppData> = context.dataStore.data
        .map { preferences ->
            preferences[APP_DATA_KEY]?.let {
                try {
                    Json.decodeFromString<AppData>(it)
                } catch (e: Exception) {
                    // In case of error, return default data
                    AppData()
                }
            } ?: AppData() // If no data is stored, return default data
        }

    suspend fun saveAppData(appData: AppData) {
        context.dataStore.edit { preferences ->
            preferences[APP_DATA_KEY] = Json.encodeToString(appData)
        }
    }
}
