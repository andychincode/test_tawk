package com.tawkto.test.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Note")
data class Note (
    @ColumnInfo(name = "login")
    var login: String,
    @ColumnInfo(name = "note")
    var note: String,
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    var id: Int? = null
)