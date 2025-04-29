package com.example.hibernatelater

import android.util.Log
import java.util.TimerTask

class ExerciseTimerTask: TimerTask {
    private lateinit var activity: MainActivity
    constructor(activity: MainActivity){
        this.activity = activity

    }
    override fun run(){
//        Log.w("MainActivity", "Inside Run")
        activity.animateBear()
    }
}