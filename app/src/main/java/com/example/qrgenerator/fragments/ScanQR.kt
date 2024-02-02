package com.example.qrgenerator.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.qrgenerator.AppDatabase
import com.example.qrgenerator.Barcode
import com.example.qrgenerator.R
import com.example.qrgenerator.ScanActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ScanQR : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var scanResult: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //launchScanner()
        val scanButton = view.findViewById<Button>(R.id.scanButton)
        scanResult = view.findViewById(R.id.scanResult)

        scanButton.setOnClickListener {
            launchScanner()
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
        return inflater.inflate(R.layout.fragment_scan_q_r, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScanQR().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun launchScanner() {
        val options = ScanOptions()
            .setOrientationLocked(false)
            .setCaptureActivity(ScanActivity::class.java)
            .setCameraId(0)
            .setBeepEnabled(false)
            .setBarcodeImageEnabled(true)
            .setDesiredBarcodeFormats(ScanOptions.QR_CODE)

        barcodeLauncher.launch(options)
    }

    private val barcodeLauncher =
        registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                scanResult.text = "Cancelled"
            } else {
                scanResult.text = result.contents
                addBarcodeResultOnDatabase(result.contents)
                Toast.makeText(activity, "Scanned: " + result.contents, Toast.LENGTH_LONG).show()
            }
        }

    private fun addBarcodeResultOnDatabase(result: String) {
        val timeInMill = System.currentTimeMillis()
        val currentTime = Timestamp(timeInMill).toString()
        val barcode = Barcode(timeInMill, currentTime, "Scanned", result)

        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getBarcodeDatabase(requireContext()).barcodeDao()
            db.addBarcode(barcode)
        }

        CoroutineScope(Dispatchers.IO).launch {
            val db = Firebase.firestore
            db.collection("barcodes")
                .document(timeInMill.toString())
                .set(barcode)
        }
    }
}