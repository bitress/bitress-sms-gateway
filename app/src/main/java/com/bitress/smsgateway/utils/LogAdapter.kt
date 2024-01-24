package com.bitress.smsgateway.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bitress.smsgateway.R
import java.text.SimpleDateFormat
import java.util.Date

class LogAdapter(private val logList: MutableList<Logs>) : Adapter<LogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_view, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = logList[position]

        val timestamp = itemsViewModel.timestamp
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formattedDate = sdf.format(Date(timestamp))

        holder.dateView.text = formattedDate
        holder.textView.text = itemsViewModel.logText
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return logList.size
    }

    fun addLog(log: Logs) {
        logList.add(log)
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.txtMsg)
        val dateView: TextView = itemView.findViewById(R.id.txtDate)
    }
}