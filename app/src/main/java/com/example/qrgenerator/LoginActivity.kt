package com.example.qrgenerator

import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import com.example.qrgenerator.databinding.ActivityLoginBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private const val TAG = "LoginActivity"

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding
    private lateinit var receiver: AutoStart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startService(Intent(this, Launcher::class.java))
        receiver = AutoStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                receiver,
                IntentFilter(Intent.ACTION_POWER_CONNECTED),
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(receiver, IntentFilter(Intent.ACTION_POWER_CONNECTED))
        }

        auth = Firebase.auth

        binding.loginButton.setOnClickListener {
            val email = binding.loginEmail.text.toString()
            val password = binding.loginPassword.text.toString()

            if (email.isNotBlank() && password.isNotBlank()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {

                        Log.d(TAG, "signInWithEmailAndPassword: success")
                        Toast.makeText(this, "success", Toast.LENGTH_SHORT).show()
                        getSharedPreferences("qr", MODE_PRIVATE).edit {
                            putString("uid", auth.currentUser!!.uid)
                            putString("name", auth.currentUser!!.displayName)
                            commit()
                        }
                        val intent = Intent(this, BaseActivity::class.java)
                        intent.putExtra("userName", auth.currentUser?.displayName)
                        intent.putExtra("mode", "Night")
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        Log.d(TAG, "signInWithEmailAndPassword: failed", it.cause)
                    }
            } else {
                Toast.makeText(this, "Enter valid email and password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.gotoSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build(),
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()

        binding.signInWithGoogle.setOnClickListener {
            signInLauncher.launch(signInIntent)
        }

    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract(),
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            getSharedPreferences("qr", MODE_PRIVATE).edit {
                putString("uid", user!!.uid)
                putString("name", user.displayName)
                commit()
            }
            val intent = Intent(this, BaseActivity::class.java)
            intent.putExtra("userName", user!!.displayName)
            intent.putExtra("userProfilePictureUrl", user.photoUrl.toString())
            intent.putExtra("mode", "Night")
            startActivity(intent)
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, BaseActivity::class.java)
            intent.putExtra("userName", user!!.displayName)
            intent.putExtra("userProfilePictureUrl", user.photoUrl.toString())
            intent.putExtra("mode", "Night")
            startActivity(intent)
            finish()
        }

    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

}