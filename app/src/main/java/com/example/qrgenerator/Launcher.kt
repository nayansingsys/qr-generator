package com.example.qrgenerator

import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.AsyncTask
import android.os.Handler
import android.os.IBinder
import android.util.Log


class Launcher : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val handler = Handler()
        handler.postDelayed({
            Log.d("Service: ", "AutoStart:")
            //val i = Intent(this, LoginActivity::class.java)
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            //startActivity(i)
        }, 5000)
        return super.onStartCommand(intent, flags, startId)
    }
}