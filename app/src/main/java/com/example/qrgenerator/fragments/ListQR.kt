package com.example.qrgenerator.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.qrgenerator.AppDatabase
import com.example.qrgenerator.Barcode
import com.example.qrgenerator.MyAdapter
import com.example.qrgenerator.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.io.IOException


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ListQR : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var myRecyclerView: RecyclerView
    private lateinit var data: List<Barcode>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val swipeRefreshLayout =
            requireView().findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener {
            swipeRefreshLayout.isRefreshing = false
            rearrangeItems()
        }

        myRecyclerView = view.findViewById(R.id.recyclerView)
        getListOfBarcodes()
        val shareCsvButton = view.findViewById<Button>(R.id.shareCsvButton)
        shareCsvButton.setOnClickListener {
            exportAndShareCsv(requireContext(), data, "barcode.csv")
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
        return inflater.inflate(R.layout.fragment_list_q_r, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListQR().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getListOfBarcodes() {
        val db = AppDatabase.getBarcodeDatabase(requireContext()).barcodeDao()
        CoroutineScope(Dispatchers.IO).launch {
            data = db.getAllBarcodes()
            CoroutineScope(Dispatchers.Main).launch {
                setAdapterOnRecyclerView()
            }
        }
    }

    private fun setAdapterOnRecyclerView() {
        val myAdapter = MyAdapter(requireContext(), data as MutableList<Barcode>)
        myRecyclerView.adapter = myAdapter
        myRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun rearrangeItems() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = Firebase.firestore
            db.collection("barcodes")
                .get()
                .addOnSuccessListener { it ->
                    val documents = it.documents
                    val myData = mutableListOf<Barcode>()
                    documents.forEach {
                        myData.add(Barcode(it.data!!))
                    }

                    CoroutineScope(Dispatchers.Main).launch {
                        val rdb = AppDatabase.getBarcodeDatabase(requireContext()).barcodeDao()
                        val temp = data
                        CoroutineScope(Dispatchers.IO).launch {
                            temp.forEach {
                                if (!myData.contains(it)) {
                                    rdb.deleteBarcode(it)
                                }
                            }
                            myData.forEach {
                                if (!temp.contains(it)) {
                                    rdb.addBarcode(it)
                                }
                            }
                        }
                        data = myData
                        setAdapterOnRecyclerView()
                    }
                    //Log.d("ListQR: ",documents.toString())
                }
        }
    }

    private fun exportAndShareCsv(context: Context, barcodes: List<Barcode>, fileName: String) {
        val csvContent = barcodesToCsv(barcodes)
        val csvFile = saveCsvFile(context, fileName, csvContent)

        if (csvFile != null) {
            val uri =
                FileProvider.getUriForFile(context, context.packageName, csvFile)

            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/csv"
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            context.startActivity(Intent.createChooser(shareIntent, "Share CSV"))
        } else {
            // Handle the case when file creation fails
            Toast.makeText(context, "Failed to create CSV file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCsvFile(context: Context, fileName: String, csvContent: String): File? {
        try {
            val directory =
                File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "CSVFiles")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)
            val fileWriter = FileWriter(file)
            fileWriter.write(csvContent)
            fileWriter.close()

            return file
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }

    private fun barcodesToCsv(barcodes: List<Barcode>): String {
        val csvContent = StringBuilder()

        // Add CSV header
        csvContent.append("ID,Date,Type,Result\n")

        for (barcode in barcodes) {
            // Escape commas in the result field if necessary
            val escapedResult = barcode.result.replace(",", "\\,")
            csvContent.append("${barcode.id},${barcode.date},${barcode.type},${escapedResult}\n")
        }

        return csvContent.toString()
    }
}
