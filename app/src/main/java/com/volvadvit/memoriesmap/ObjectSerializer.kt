package com.volvadvit.memoriesmap

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

class ObjectSerializer(private val context: Context) {

    private val listType = Types.newParameterizedType(List::class.java, MutableList::class.java, String::class.java)
    private val jsonAdapter: JsonAdapter<List<MutableList<String>>> = Moshi.Builder().build().adapter(listType)
    private val sPref = context.getSharedPreferences("map-marks", AppCompatActivity.MODE_PRIVATE)

    internal fun fillListFromPreferences() {
        val json = sPref.getString("map-marks", "") ?: ""
        var listOfLists: List<MutableList<String>>? = null
        if (json != "") {
            listOfLists = jsonAdapter.fromJson(json)!!
        }
        if (!listOfLists.isNullOrEmpty()) {
            MainActivity.listAddress = listOfLists[0]
            MainActivity.listLocation = listOfLists[1]
        } else {
            Toast.makeText(context, "Empty user's data", Toast.LENGTH_SHORT).show()
        }
    }

    internal fun saveData() {
        sPref.edit().putString("map-marks",
            jsonAdapter.toJson(listOf(MainActivity.listAddress, MainActivity.listLocation))
        ).apply()
    }
}