package com.example.hibernatelater

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

class Calendar {
    private var pref : SharedPreferences
    private var calendarInstance = Calendar.getInstance()
    private var selectedYear = 0
    // month is always one less than actual month b/c of how built-in works (ex. jan is 0)
    private var selectedMonth = 0
    private var selectedDay = 0

    constructor(context : Context){
        pref = context.getSharedPreferences( context.packageName + "_preferences", Context.MODE_PRIVATE )
        calendarInstance = Calendar.getInstance()
        selectedYear = calendarInstance.get(Calendar.YEAR)
        selectedMonth = calendarInstance.get(Calendar.MONTH)
        selectedDay = calendarInstance.get(Calendar.DAY_OF_MONTH)
    }

    fun getSelectedYear() : Int {
        return this.selectedYear
    }

    fun setSelectedYear(year : Int){
        this.selectedYear = year
    }

    fun getSelectedMonth() : Int {
        return this.selectedMonth
    }

    fun setSelectedMonth(month : Int){
        this.selectedMonth = month
    }

    fun getSelectedDay() : Int {
        return this.selectedDay
    }

    fun setSelectedDay(day : Int){
        this.selectedDay = day
    }

    fun getRatingCurrDay() : Float{
        return pref.getFloat("$selectedYear, ${selectedMonth+1}, $selectedDay", -1F)
    }

    fun getRatingOnDay(year : Int, month : Int, day : Int): Float{
        return pref.getFloat("$year, ${month+1}, $day", -1F)
    }

    fun setRating(year : Int, month : Int, day : Int, rating : Float){
        var editor : SharedPreferences.Editor = pref.edit()
        editor.putFloat("$year, ${month + 1}, $day", rating)
        editor.commit()
    }

    fun getFormattedDate() : String{
        return "${selectedMonth+1}/$selectedDay/$selectedYear"
    }
}