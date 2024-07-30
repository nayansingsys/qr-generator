package com.example.qrgenerator

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class AutoStart : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("AutoStart: ","Power Connected")
        //val i = Intent(context, LoginActivity::class.java)
        //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //context?.startActivity(i)
    }
}