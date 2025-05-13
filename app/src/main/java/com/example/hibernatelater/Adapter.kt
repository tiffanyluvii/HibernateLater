package com.example.hibernatelater

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class Adapter(context: Context, private val users: MutableList<MainActivity.User>) :
    ArrayAdapter<MainActivity.User>(context, R.layout.item_leaderboard, users) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.item_leaderboard, parent, false)

            val user = getItem(position)!!

            view.findViewById<TextView>(R.id.rank).text = "${position + 1}"
            view.findViewById<TextView>(R.id.name).text = user.name
            view.findViewById<TextView>(R.id.streak).text = "${user.streak} days"

            return view
        }

        fun update(new : List<MainActivity.User>) {
            users.clear()
            users.addAll(new)
            notifyDataSetChanged()
        }
    }
