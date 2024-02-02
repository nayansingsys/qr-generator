package com.example.qrgenerator

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.qrgenerator.databinding.ActivityChatBinding
import com.firebase.ui.auth.ui.phone.CountryListSpinner.DialogPopup
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private var chats = mutableListOf<Chat>()
    private lateinit var myChatAdapter: MyChatAdapter
    private var uid: String? = null
    private lateinit var db: FirebaseFirestore
    private  var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = Firebase.firestore
        val sp = getSharedPreferences("qr", MODE_PRIVATE)
        uid = sp.getString("uid", null)
        val name = sp.getString("name", null)
        setAdapter()
        setList()



        binding.swipeRefreshLayout.setOnRefreshListener {
            setList()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.sendButton.setOnClickListener {
            val msg = binding.message.text.toString()

            if (msg.isNotBlank() && msg.isNotEmpty()) {
                val chat = Chat(uid!!, name!!, msg)
                binding.message.text.clear()
                chats.add(chat)
                myChatAdapter.notifyItemInserted(index)
                index = chats.size

                CoroutineScope(Dispatchers.IO).launch {
                    val data = mapOf("data" to chats)
                    db.collection("chats")
                        .document("groupChat")
                        .set(data)
                }
                val ss = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                ss.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            }
        }
    }

    private fun setList(){
        CoroutineScope(Dispatchers.IO).launch {
            db.collection("chats")
                .document("groupChat")
                .get()
                .addOnSuccessListener { res ->
                    val data = res.data?.get("data") as MutableList<*>
                    for(i in index until data.size) {
                        chats.add(Chat(data[i] as Map<*, *>))
                        myChatAdapter.notifyItemInserted(i)
                    }
                    index = chats.size
                }
        }
    }

    private fun setAdapter() {
        myChatAdapter = MyChatAdapter(this, chats, uid!!)
        binding.chatRecyclerView.adapter = myChatAdapter
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
    }

}