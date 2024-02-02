package com.example.qrgenerator

import android.content.Context
import android.content.res.Resources.Theme
import android.graphics.Color
import android.graphics.Paint.Style
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.annotations.concurrent.Background

class MyChatAdapter(private val context: Context,private val data: MutableList<Chat>,private val uid: String):
    RecyclerView.Adapter<MyChatAdapter.MyViewHolder>() {
    var lastUser: String = "No"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.chat_view,parent,false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currData = data[position]
        if(lastUser == currData.name){
            holder.name.visibility = View.GONE
        }
        lastUser = currData.name

        if(currData.id == uid){
            holder.root.gravity = Gravity.END

            holder.name.text = "You"
            holder.message.text = currData.message
            holder.message.background = ContextCompat.getDrawable(context,R.drawable.bg_self_chat)
        }else{
            holder.name.text = currData.name
            holder.message.text = currData.message
            holder.message.background = ContextCompat.getDrawable(context,R.drawable.bg_other_chat)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val name: TextView
        val message: TextView
        val root: LinearLayout

        init {
            name = itemView.findViewById(R.id.chatUserName)
            message = itemView.findViewById(R.id.chatUserMessage)
            root = itemView.findViewById(R.id.rootChatView)
        }
    }
}