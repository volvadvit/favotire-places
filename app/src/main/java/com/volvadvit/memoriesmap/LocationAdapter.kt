package com.volvadvit.memoriesmap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(private val list: MutableList<String>,
                      private val listener: (Int) -> Unit):
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>(){

    class LocationViewHolder(item: View): RecyclerView.ViewHolder(item){
        val textLocation: TextView = item.findViewById(R.id.item_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LocationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false))

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.textLocation.text = list[position]
        holder.itemView.setOnClickListener { listener(position) }
    }

    override fun getItemCount() = list.size
}