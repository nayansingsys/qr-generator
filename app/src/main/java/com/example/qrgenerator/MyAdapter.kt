package com.example.qrgenerator

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAdapter(private val context: Context, private val data: MutableList<Barcode>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView =
            LayoutInflater.from(context).inflate(R.layout.list_barcode_item, parent, false)
        return MyViewHolder(itemView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val currBarcode = data[position]

        holder.date.text = "Date: " + currBarcode.date
        holder.type.text = "Type: " + currBarcode.type
        holder.result.text = "Result: " + currBarcode.result
        holder.button.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getBarcodeDatabase(context).barcodeDao()
                db.deleteBarcode(currBarcode)
            }
            CoroutineScope(Dispatchers.IO).launch {
                val db = Firebase.firestore
                db.collection("barcodes")
                    .document(currBarcode.id.toString())
                    .delete()
            }

            data.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var date: TextView
        var type: TextView
        var result: TextView
        var button: Button

        init {
            date = itemView.findViewById(R.id.date)
            type = itemView.findViewById(R.id.type)
            result = itemView.findViewById(R.id.result)
            button = itemView.findViewById(R.id.button)
        }
    }


}