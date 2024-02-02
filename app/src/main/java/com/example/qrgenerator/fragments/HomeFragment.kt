package com.example.qrgenerator.fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.qrgenerator.AppDatabase
import com.example.qrgenerator.CalendarActivity
import com.example.qrgenerator.ChatActivity
import com.example.qrgenerator.CompassActivity
import com.example.qrgenerator.LoginActivity
import com.example.qrgenerator.Note
import com.example.qrgenerator.R
import com.firebase.ui.auth.AuthUI
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var listView: ListView
    private lateinit var data: List<Note>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.homeListView)
        val userName = requireActivity().intent.getStringExtra("userName")
        val userProfilePictureUrl = requireActivity().intent.getStringExtra("userProfilePictureUrl")

        val userNameView = view.findViewById<TextView>(R.id.homePageUserName)
        val userProfilePictureView = view.findViewById<ImageView>(R.id.homePageProfilePicture)
        val signOutButton = view.findViewById<Button>(R.id.homePageSignOutButton)

        userNameView.text = userName
        Picasso.get().load(userProfilePictureUrl).into(userProfilePictureView)

        signOutButton.setOnClickListener {
            signOut()
        }
        askNotificationPermission()
        val db = AppDatabase.getNoteDatabase(requireContext()).barcodeDao()
        CoroutineScope(Dispatchers.IO).launch {
            data = db.getAllNotes()
            CoroutineScope(Dispatchers.Main).launch {
                setNotes()
            }
        }

        val clearNotesButton = view.findViewById<Button>(R.id.homeDeleteNotesButton)
        clearNotesButton.setOnClickListener {
            val db = AppDatabase.getNoteDatabase(requireContext()).barcodeDao()
            CoroutineScope(Dispatchers.IO).launch {
                db.deleteAllNotes()
            }
            data = listOf()
            setNotes()
        }
        val compassButton = view.findViewById<Button>(R.id.compassButton)
        compassButton.setOnClickListener {
            val intent = Intent(requireContext(), CompassActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.chatButton).setOnClickListener {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
        }
        view.findViewById<Button>(R.id.getCalendarEventsButton).setOnClickListener {
            startActivity(Intent(requireContext(), CalendarActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun setNotes() {
        val notes = arrayListOf<String>()
        data.forEach {
            notes.add(it.data)
        }
        adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, notes)
        listView.adapter = adapter
        setListViewHeightBasedOnChildren(listView)
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(requireContext())
            .addOnSuccessListener {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(requireContext(), "success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun askNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) ==
                PackageManager.PERMISSION_GRANTED
            ) {

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setListViewHeightBasedOnChildren(listView: ListView) {
        val listAdapter = listView.adapter
            ?: // pre-condition
            return
        var totalHeight = 0
        for (i in 0 until listAdapter.count) {
            val listItem = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        val params = listView.layoutParams
        params.height = totalHeight + listView.dividerHeight * (listAdapter.count - 1)
        listView.layoutParams = params
        listView.requestLayout()
    }

}