package com.example.hibernatelater

import android.content.Context

class HomePage {
    private var check_exercise: Boolean = false // is the user exercising currently
    private var check_break: Boolean = false // is the user currently on break
    private var currentExercise: Int = 1
    private lateinit var listOfExercises: ArrayList<Exercise>

    constructor(context:Context){
        this.listOfExercises = ArrayList<Exercise>()
    }

    fun startExercise(){
        this.check_exercise = true
    }

    fun endExercise(){
        this.check_exercise = false
    }

    fun currentlyExercising(): Boolean{
        return this.check_exercise
    }

    fun startBreak(){
        this.check_break = true
    }

    fun endBreak(){
        this.check_break = false
    }

    fun currentlyOnBreak(): Boolean{
        return this.check_break
    }

    fun getCurrentExerciseNumber(): Int{
        return this.currentExercise
    }

    fun incrementExerciseNumber(){
        this.currentExercise += 1
    }

    fun addToList(exercise: Exercise){
        this.listOfExercises.add(exercise)
    }

    fun resetExerciseNumber(){
        this.currentExercise = 1
    }

    fun clearArrayList(){
        this.listOfExercises.clear()
    }
}