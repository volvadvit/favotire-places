package com.volvadvit.memoriesmap.common

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.util.*

class GeoAsyncTask(private val context: Context) : AsyncTask<LatLng, Void, String>() {

    override fun doInBackground(vararg params: LatLng?): String {
       return getAreaInfo(params[0]!!)
    }

    private fun getAreaInfo(location: LatLng): String {
        var address = ""
        val geoCoder = Geocoder(context.applicationContext, Locale.getDefault())
        val listAddresses: MutableList<Address> =
            try {
                geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude, 1
                )
            } catch (e: Exception) {
                Log.d("Exception", e.message.toString())
                mutableListOf<Address>()
            }

        if (listAddresses.isNotEmpty()) {
            if (!listAddresses[0].thoroughfare.isNullOrEmpty()) {
                address = listAddresses[0].thoroughfare + " "
            }
            if (!listAddresses[0].subThoroughfare.isNullOrEmpty()) {
                address += listAddresses[0].subThoroughfare + " "
            }
            if (!listAddresses[0].subLocality.isNullOrEmpty()) {
                address += listAddresses[0].subLocality + " "
            }
            if (!listAddresses[0].adminArea.isNullOrEmpty()) {
                address += listAddresses[0].adminArea + " "
            }
        }
        return address
    }
}