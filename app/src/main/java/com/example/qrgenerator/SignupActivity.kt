package com.example.qrgenerator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.qrgenerator.databinding.ActivitySignupBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private const val TAG = "SignupActivity"

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        
                        Log.d(TAG, "createUserWithEmailAndPassword: success")
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, BaseActivity::class.java)
                        intent.putExtra("userName", auth.currentUser?.displayName)
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "createUserWithEmailAndPassword: failed", it.cause)
                    }
            } else {
                Toast.makeText(this, "Enter Valid Input.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.gotoLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}