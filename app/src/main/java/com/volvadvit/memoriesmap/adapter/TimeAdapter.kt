package com.volvadvit.memoriesmap.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.volvadvit.memoriesmap.R
import com.volvadvit.memoriesmap.activity.MainActivity

class TimeAdapter(private val timeSet: MutableSet<String>):
    RecyclerView.Adapter<TimeAdapter.TimeViewHolder>(){

    companion object {
        internal var recyclerLocation: RecyclerView? = null
    }

    class TimeViewHolder(item: View): RecyclerView.ViewHolder(item){
        val textTime: TextView = item.findViewById(R.id.time_text)
        val recyclerLoc: RecyclerView = item.findViewById(R.id.recycler_location)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TimeViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_time, parent, false))

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        recyclerLocation = holder.recyclerLoc
        val timeStamp = timeSet.elementAt(position)
        holder.textTime.text = timeStamp
        val indexList = mutableListOf<Int>()
        for (i in 0 until MainActivity.listTimeStamp.size) {
            if (MainActivity.listTimeStamp[i] == timeStamp) {
                indexList.add(i)
            }
        }
        val areaList = MainActivity.listAddress.slice(indexList)
        val locAdapter = LocationAdapter(areaList.toMutableList())
        holder.recyclerLoc.adapter = locAdapter
        locAdapter.notifyDataSetChanged()
    }

    override fun getItemCount() = timeSet.size
}