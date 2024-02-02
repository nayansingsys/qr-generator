package com.example.qrgenerator

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.qrgenerator.databinding.ActivityBaseBinding
import com.example.qrgenerator.fragments.GenerateQR
import com.example.qrgenerator.fragments.HomeFragment
import com.example.qrgenerator.fragments.ListQR
import com.example.qrgenerator.fragments.NoteFragment
import com.example.qrgenerator.fragments.ScanQR
import com.firebase.ui.auth.AuthUI

class BaseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBaseBinding
    private var backPressedTime = 0L
    lateinit var actionBar: ActionBar
    private var mode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(intent.getIntExtra("theme", R.style.Base_Theme_QRGenerator))
        binding = ActivityBaseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        mode = intent.getStringExtra("mode")
        if (mode.isNullOrEmpty()) {
            mode = "Night"
        }


        actionBar = supportActionBar!!

        var title = intent.getStringExtra("title")
        if (title.isNullOrEmpty()) title = "Home"
        binding.navigation.selectedItemId = getItemIdFromTitle(title)
        setFragment(title, getFragmentFromTitle(title))


        binding.navigation.setOnItemSelectedListener {
            setFragment(getTitle(it.itemId), getFragmentFromId(it.itemId))
            true
        }

    }

    private fun setFragment(title: String, fragment: Fragment) {
        actionBar.title = title
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun getItemIdFromTitle(title: String): Int {
        return when (title) {
            "Home" -> R.id.nav_home
            "Scan QR" -> R.id.nav_scan_qr
            "Generate QR" -> R.id.nav_generate_qr
            "Barcodes" -> R.id.nav_list
            "Notepad" -> R.id.nav_note
            else -> R.id.nav_home
        }
    }

    private fun getTitle(id: Int): String {
        return when (id) {
            R.id.nav_home -> "Home"
            R.id.nav_scan_qr -> "Scan QR"
            R.id.nav_generate_qr -> "Generate QR"
            R.id.nav_list -> "Barcodes"
            R.id.nav_note -> "Notepad"
            else -> "Home"
        }
    }

    private fun getFragmentFromTitle(title: String): Fragment {
        return when (title) {
            "Home" -> HomeFragment()
            "Scan QR" -> ScanQR()
            "Generate QR" -> GenerateQR()
            "Barcodes" -> ListQR()
            "Notepad" -> NoteFragment()
            else -> HomeFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_bar_menu, menu)
        val item = menu!!.findItem(R.id.action_favorite)
        item.setActionView(R.layout.switch_layout)
        val mySwitch = item.actionView!!.findViewById<SwitchCompat>(R.id.switch2)

        if (mode == "Night") {
            mySwitch.isChecked = true
            mySwitch.text = "Night"
        } else {
            mySwitch.text = "Day"
        }
        mySwitch.setOnCheckedChangeListener { compoundButton, b ->
            if (compoundButton.isChecked) {
                updateTheme(android.R.style.ThemeOverlay_Material_Dark, "Night")
            } else {
                updateTheme(android.R.style.ThemeOverlay_Material_Light, "Day")
            }
        }
        return super.onCreateOptionsMenu(menu)
    }


    private fun updateTheme(theme: Int, mode: String) {
        val userName = intent.getStringExtra("userName")
        val userProfilePictureUrl = intent.getStringExtra("userProfilePictureUrl")

        val intent = Intent(this, BaseActivity::class.java)
        intent.putExtra("title", actionBar.title)
        intent.putExtra("theme", theme)
        intent.putExtra("mode", mode)
        intent.putExtra("userName", userName)
        intent.putExtra("userProfilePictureUrl", userProfilePictureUrl)

        //val option = ActivityOptions.makeSceneTransitionAnimation(this)
        startActivity(intent)
        overridePendingTransition(0,0)
        finish()
    }


    private fun getFragmentFromId(id: Int): Fragment {
        return when (id) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_scan_qr -> ScanQR()
            R.id.nav_generate_qr -> GenerateQR()
            R.id.nav_list -> ListQR()
            R.id.nav_note -> NoteFragment()
            else -> Fragment()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (backPressedTime + 3000 > System.currentTimeMillis()) {
            AuthUI.getInstance().signOut(this)
                .addOnSuccessListener {
                    finish()
                }
        } else {
            Toast.makeText(this, "back again", Toast.LENGTH_SHORT).show()
        }
        backPressedTime = System.currentTimeMillis()
    }
}