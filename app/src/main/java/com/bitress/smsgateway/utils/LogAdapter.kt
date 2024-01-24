package com.bitress.smsgateway.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import com.bitress.smsgateway.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogAdapter(private val logList: MutableList<Logs>) : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.log_view, parent, false)
        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemsViewModel = logList[position]
        holder.textView.text = itemsViewModel.logText
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return logList.size
    }

    fun addLog(log: Logs) {
        logList.add(log)
    }


    fun getCurrentTime(): String {
        val currentTimeMillis = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDate = Date(currentTimeMillis)
        return dateFormat.format(currentDate)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
    }
}