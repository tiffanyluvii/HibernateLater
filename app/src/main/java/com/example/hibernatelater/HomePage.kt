package com.example.hibernatelater

import android.content.Context

class HomePage {
    var check_exercise: Boolean = false // is the user exercising currently
    var check_break: Boolean = false // is the user currently on break

    constructor(context:Context){

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


}