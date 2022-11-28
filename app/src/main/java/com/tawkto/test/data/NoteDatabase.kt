package com.tawkto.test.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tawkto.test.model.Note

@Database(entities = [Note::class], version = 1, exportSchema = true)
abstract class NoteDatabase: RoomDatabase() {
    abstract fun noteDao() : NoteDao

    companion object {
        @Volatile
        private var instance: NoteDatabase? = null

        @Synchronized
        fun getInstance(context: Context): NoteDatabase {
            if (instance == null)
                instance = Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java, "NOTE_DATABASE")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()

            return instance!!
        }

        private val roomCallback = object : Callback() { }
    }
}