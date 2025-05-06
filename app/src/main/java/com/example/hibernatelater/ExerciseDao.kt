package com.example.hibernatelater
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.lifecycle.LiveData

@Dao
interface ExerciseDao {
    @Insert
    suspend fun insert(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises ORDER BY date DESC")
    fun getAllExercises(): LiveData<List<ExerciseEntity>>

    @Query("DELETE FROM exercises")
    suspend fun clearAllExercises()
}