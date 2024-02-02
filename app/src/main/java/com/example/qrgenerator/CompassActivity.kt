package com.example.qrgenerator

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.roundToInt

class CompassActivity : AppCompatActivity() , SensorEventListener{
    private lateinit var sensorManage: SensorManager
    private lateinit var compassImage: ImageView
    private lateinit var degreeTV: TextView
    private var degreeStart: Float = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compass)

        compassImage = findViewById(R.id.compass_image)
        degreeTV = findViewById(R.id.degreeTV)
        degreeStart = 0f

        sensorManage = getSystemService(SENSOR_SERVICE) as SensorManager


    }

    override fun onPause() {
        super.onPause()
        sensorManage.unregisterListener(this)
    }

    override fun onResume() {
        super.onResume()
        sensorManage.registerListener(this, sensorManage.getDefaultSensor(Sensor.TYPE_ORIENTATION),
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        val degree = event?.values!![0].roundToInt().toFloat()
        degreeTV.text = "Heading: $degree degrees"

        val ra = RotateAnimation(
            degreeStart,
            -degree,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )

        ra.fillAfter = true
        ra.duration = 210

        compassImage.startAnimation(ra)
        degreeStart = -degree
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}

