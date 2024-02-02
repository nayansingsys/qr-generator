package com.example.qrgenerator.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import com.example.qrgenerator.AppDatabase
import com.example.qrgenerator.Note
import com.example.qrgenerator.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class NoteFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val saveButton = view.findViewById<FloatingActionButton>(R.id.noteSaveButton)
        val noteTextField = view.findViewById<EditText>(R.id.noteTextField)

        saveButton.setOnClickListener {
            if (noteTextField.text.isNotEmpty()) {
                val note = Note(0, noteTextField.text.toString())
                val db = AppDatabase.getNoteDatabase(requireContext()).barcodeDao()
                CoroutineScope(Dispatchers.IO).launch {
                    db.addNote(note)
                }
                noteTextField.text.clear()
                Toast.makeText(requireContext(), "Save", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Enter some text", Toast.LENGTH_SHORT).show()
            }
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
        return inflater.inflate(R.layout.fragment_note, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NoteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}