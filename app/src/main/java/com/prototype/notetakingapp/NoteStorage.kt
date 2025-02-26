package com.prototype.notetakingapp

import android.content.Context
import android.content.SharedPreferences

object NoteStorage {
    private const val PREFS_NAME = "notes_prefs"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveNote(context: Context, title: String, content: String) {
        getPrefs(context).edit().putString(title, content).apply()
    }

    fun getAllNotes(context: Context): Map<String, String> {
        return getPrefs(context).all.mapValues { it.value.toString() }
    }

    fun deleteLatestNote(context: Context) {
        val prefs = getPrefs(context)
        val notes = prefs.all.mapValues { it.value.toString() }

        if (notes.isNotEmpty()) {
            val latestNoteTitle = notes.keys.last()
            prefs.edit().remove(latestNoteTitle).apply()

            if (prefs.all.isEmpty()) {
                prefs.edit().clear().apply()
            }
        }
    }
}