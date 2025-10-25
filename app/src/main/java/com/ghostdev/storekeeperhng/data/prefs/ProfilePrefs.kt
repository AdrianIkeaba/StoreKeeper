package com.ghostdev.storekeeperhng.data.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfilePrefs(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("profile_prefs", Context.MODE_PRIVATE)

    private val scope = CoroutineScope(Dispatchers.IO)

    private val _userName = MutableStateFlow(prefs.getString(KEY_USER_NAME, "") ?: "")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _storeName = MutableStateFlow(prefs.getString(KEY_STORE_NAME, "") ?: "")
    val storeName: StateFlow<String> = _storeName.asStateFlow()

    private val _profileImagePath = MutableStateFlow(prefs.getString(KEY_PROFILE_IMAGE, null))
    val profileImagePath: StateFlow<String?> = _profileImagePath.asStateFlow()

    private val _categories = MutableStateFlow(loadCategories())
    val categories: StateFlow<Set<String>> = _categories.asStateFlow()

    private val _firstRun = MutableStateFlow(prefs.getBoolean(KEY_FIRST_RUN, true))
    val firstRun: StateFlow<Boolean> = _firstRun.asStateFlow()

    fun setUserName(name: String) {
        scope.launch {
            prefs.edit().putString(KEY_USER_NAME, name).apply()
            _userName.emit(name)
        }
    }

    fun setStoreName(name: String) {
        scope.launch {
            prefs.edit().putString(KEY_STORE_NAME, name).apply()
            _storeName.emit(name)
        }
    }

    fun setProfileImagePath(path: String?) {
        scope.launch {
            prefs.edit().putString(KEY_PROFILE_IMAGE, path).apply()
            _profileImagePath.emit(path)
        }
    }

    fun setFirstRun(fr: Boolean) {
        scope.launch {
            prefs.edit().putBoolean(KEY_FIRST_RUN, fr).apply()
            _firstRun.emit(fr)
        }
    }

    fun addCategory(new: String) {
        val trimmed = new.trim()
        if (trimmed.isEmpty()) return
        scope.launch {
            val updated = (_categories.value + trimmed).sorted().toSet()
            saveCategories(updated)
            _categories.emit(updated)
        }
    }

    fun removeCategory(cat: String) {
        scope.launch {
            val updated = _categories.value - cat
            saveCategories(updated)
            _categories.emit(updated)
        }
    }

    fun resetAll(context: Context) {
        scope.launch {
            // Clear prefs
            prefs.edit().clear().apply()
            _userName.emit("")
            _storeName.emit("")
            _profileImagePath.emit(null)
            _categories.emit(emptySet())
            _firstRun.emit(true)

            // Delete images directory
            try {
                val dir = java.io.File(context.filesDir, "images")
                if (dir.exists()) dir.deleteRecursively()
            } catch (_: Exception) {}

            // Delete Room database file
            try {
                context.deleteDatabase("storekeeper.db")
            } catch (_: Exception) {}
        }
    }

    private fun loadCategories(): Set<String> {
        val csv = prefs.getString(KEY_CATEGORIES, null) ?: return emptySet()
        return csv.split('|').map { it.trim() }.filter { it.isNotEmpty() }.toSet()
    }

    private fun saveCategories(cats: Set<String>) {
        val csv = cats.joinToString("|")
        prefs.edit().putString(KEY_CATEGORIES, csv).apply()
    }

    companion object {
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_STORE_NAME = "store_name"
        private const val KEY_PROFILE_IMAGE = "profile_image"
        private const val KEY_CATEGORIES = "categories_csv"
        private const val KEY_FIRST_RUN = "first_run"
    }
}
