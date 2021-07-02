package com.volvadvit.memoriesmap

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(private val list: MutableList<String>):
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){

    class LocationViewHolder(item: View): RecyclerView.ViewHolder(item){
        val textLocation: TextView = item.findViewById(R.id.item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.textLocation.text = list[position] + " " + MainActivity.timeStampMap.get(MainActivity.listLocation[position])

        val view = holder.itemView
        view.setOnClickListener {
                val intent = Intent(view.context, MapsActivity::class.java)
                if (MainActivity.listLocation.isNotEmpty()) {
                    intent.putExtra("Location", position.toString())
                } else {
                    intent.putExtra("Location", "emptyExtra")
                    Toast.makeText(view.context, "Location is empty", Toast.LENGTH_SHORT).show()
                }
                view.context.startActivity(intent)
        }

        view.setOnLongClickListener {
                    object : AlertDialog.Builder(view.context) {}
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete place?")
                        .setMessage("Do you want to delete place?")
                        .setPositiveButton("Yes") { dialog, which ->
                            MainActivity.timeStampMap.remove(MainActivity.listLocation[position])
                            MainActivity.listLocation.removeAt(position)
                            MainActivity.listAddress.removeAt(position)
                            MainActivity.mAdapter.notifyDataSetChanged()

                            val objectSerializer = ObjectSerializer(view.context)
                            objectSerializer.saveData()
                        }
                        .setNegativeButton("No", null)
                        .show()
            true
        }
    }

    override fun getItemCount() = list.size
}