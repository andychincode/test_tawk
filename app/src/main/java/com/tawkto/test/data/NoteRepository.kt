package com.tawkto.test.data

import androidx.lifecycle.LiveData
import com.tawkto.test.TestApplication
import com.tawkto.test.model.Note

class NoteRepository(application: TestApplication) {

    private var noteDao: NoteDao
    private var allNotes: LiveData<List<Note>>
    private var note: LiveData<Note>? = null

    private val database = NoteDatabase.getInstance(application)

    init {
        noteDao = database.noteDao()
        allNotes = noteDao.getAllNotes()
    }

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    // get user note from room database
    fun getNote(userId: Int) : LiveData<Note>? {
        return noteDao.getNote(userId)
    }

    // get all user note
    fun getAllNotes() : LiveData<List<Note>> {
        return allNotes
    }
}