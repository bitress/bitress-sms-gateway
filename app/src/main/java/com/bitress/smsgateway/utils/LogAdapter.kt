package com.bitress.smsgateway.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bitress.smsgateway.R

class LogAdapter(private val logList: List<Logs>) : Adapter<LogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_view, parent, false)
        return ViewHolder(view)
    }


    // binds the list items to a view

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



        val ItemsViewModel = logList[position]



        // sets the text to the textview from our itemHolder class

        holder.textView.text = ItemsViewModel.logText



    }



    // return the number of the items in the list

    override fun getItemCount(): Int {

        return logList.size

    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }


}