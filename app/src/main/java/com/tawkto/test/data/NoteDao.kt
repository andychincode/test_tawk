package com.tawkto.test.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tawkto.test.model.Note

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("SELECT * FROM Note WHERE id =:userId")
    fun getNote(userId: Int) : LiveData<Note>

    @Query("SELECT * FROM Note")
    fun getAllNotes() : LiveData<List<Note>>
}