package com.example.hibernatelater

class Exercise {
    private lateinit var exerciseType: String
    private var sets: Int = 0
    private var reps: Int = 0
    private var currentSet = 1

    constructor(exerciseType: String, sets: Int, reps: Int){
        this.exerciseType = exerciseType
        this.sets = sets
        this.reps = reps
    }

    fun getExerciseType(): String{
        return this.exerciseType
    }

    fun getSets(): Int{
        return this.sets
    }

    fun getReps(): Int{
        return this.reps
    }

    fun getCurrentSet(): Int{
        return this.currentSet
    }

    fun incrementSet(){
        this.currentSet += 1
    }

    fun finishedExercise(): Boolean{
        if (this.currentSet >= this.sets){
            return true
        }
        return false
    }

}