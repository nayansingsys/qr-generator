package com.example.qrgenerator.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.qrgenerator.AppDatabase
import com.example.qrgenerator.Barcode
import com.example.qrgenerator.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.sql.Timestamp

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class GenerateQR : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var bitmap: Bitmap
    private lateinit var inputText: EditText
    private lateinit var qrImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inputText = view.findViewById(R.id.qrEditText)
        qrImageView = view.findViewById(R.id.qrImageView)
        val generateButton = view.findViewById<Button>(R.id.generateButton)
        val shareButton = view.findViewById<Button>(R.id.shareButton)

        generateButton.setOnClickListener {
            generateBarcode()
            shareButton.visibility = View.VISIBLE
        }

        shareButton.setOnClickListener {
            shareImage()
        }
        val rv = RemoteViews(context?.packageName, R.layout.notification)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generate_q_r, container, false)
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GenerateQR().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun generateBarcode() {
        val text = inputText.text.toString()

        if (text.isEmpty()) {
            Toast.makeText(activity, "Input Text", Toast.LENGTH_SHORT).show()
        } else {
            val writer = QRCodeWriter()
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }

            addBarcodeResultOnDatabase(text)
            qrImageView.setImageBitmap(bitmap)
        }
    }

    private fun getImageUriFromBitmap(bitmap: Bitmap): Uri {
        val imageFile = File(requireActivity().cacheDir, "qr_code_image.png")
        val uri = FileProvider.getUriForFile(
            requireActivity().baseContext,
            "com.example.qrgenerator",
            imageFile
        )

        try {
            val stream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return uri
    }

    private fun shareImage() {
        val uri = getImageUriFromBitmap(bitmap)
        val emailIntent = Intent(Intent.ACTION_SEND)
        emailIntent.type = "image/*"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("nayantripathi78@gmail.com"))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Code")
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Here is the QR code for you.")
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(emailIntent, "Send email"))
    }

    private fun addBarcodeResultOnDatabase(result: String) {
        val timeInMill = System.currentTimeMillis()
        val currentTime = Timestamp(timeInMill).toString()
        val barcode = Barcode(timeInMill, currentTime, "Generated", result)

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