package com.example.hibernatelater

import androidx.room.Entity
import androidx.room.PrimaryKey

// This class will manage the persistent data for the exercises
@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val sets: Int,
    val reps: Int,
    val date: String // Format: MM-DD-YYYY
)