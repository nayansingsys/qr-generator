package com.example.qrgenerator

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventAdapter(val context: Context, val data: MutableList<Event>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.event_view, parent, false)
        return EventViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val curr = data[position]
        holder.eventNameView.text = curr.name
        holder.eventStartDateView.text = curr.sDate
        holder.eventEndDateView.text = curr.eDate
    }

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameView: TextView
        val eventStartDateView: TextView
        val eventEndDateView: TextView

        init {
            eventNameView = itemView.findViewById(R.id.eventName)
            eventStartDateView = itemView.findViewById(R.id.eventStartDate)
            eventEndDateView = itemView.findViewById(R.id.eventEndDate)
        }
    }
}